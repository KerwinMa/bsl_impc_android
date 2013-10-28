package com.foreveross.chameleon.push.mina.library.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.TimeUnit;

import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.util.Base64;
import android.widget.Toast;

import com.csair.impc.R;
import com.foreveross.chameleon.TmpConstants;
import com.foreveross.chameleon.event.ConnectStatusChangeEvent;
import com.foreveross.chameleon.event.EventBus;
import com.foreveross.chameleon.push.mina.library.Constants;
import com.foreveross.chameleon.push.mina.library.filter.HeartBeatFilter;
import com.foreveross.chameleon.push.mina.library.handler.AbstractCommandHandler;
import com.foreveross.chameleon.push.mina.library.protocol.ProtobufCodecFactory;
import com.foreveross.chameleon.push.mina.library.protocol.PushProtocol.Auth_Req;
import com.foreveross.chameleon.push.mina.library.protocol.PushProtocol.Auth_Rsp;
import com.foreveross.chameleon.push.mina.library.protocol.PushProtocol.Packet;
import com.foreveross.chameleon.push.mina.library.service.MinaPushService;
import com.foreveross.chameleon.push.mina.library.util.IdGenerator;
import com.foreveross.chameleon.push.mina.library.util.MapLoader;
import com.foreveross.chameleon.push.mina.library.util.NetworkUtil;
import com.foreveross.chameleon.push.mina.library.util.PropertiesUtil;
import com.foreveross.chameleon.push.mina.library.util.RSACoder;
import com.foreveross.chameleon.push.mina.library.util.SessionHelper;
import com.foreveross.chameleon.util.SharedPreferencesUtil;
import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import com.squareup.otto.ThreadEnforcer;

/**
 * [Mina Mobile连接]<BR>
 * [功能详细描述]
 * 
 * @author 冯伟立
 * @version [DVRTest, 2013-6-21]
 */

public class MinaMobileClient extends IoHandlerAdapter implements Runnable {
	private final static Logger log = LoggerFactory
			.getLogger(MinaMobileClient.class);
	/**
	 * [Mina服务器端口]
	 */
	private static int SERVER_PORT = 0;
	/**
	 * [Mina服务器IP]
	 */
	private static String SERVER_IP = "127.0.0.1";

	/**
	 * [作用描述]
	 */
	private InetSocketAddress socketAddress;
	/**
	 * The connector
	 * */
	private IoConnector connector;
	/**
	 * The session
	 * */
	private IoSession session;
	/**
	 * [重连线程]
	 */
	private Thread reCreateThread;

	/**
	 * [相关联服务]
	 */
	private MinaPushService minaPushService;

	/**
	 * [Mina 请求]
	 */
	private BlockingQueue<Integer> reqQueue = new ArrayBlockingQueue<Integer>(
			10);

	private ConnectFuture connectFuture;

	public MinaMobileClient(MinaPushService minaPushService) {
		this.minaPushService = minaPushService;
		/**
		 * 初始化设置值
		 */
		log.debug("load handler map from path {}", minaPushService
				.getResources().getResourceName(Constants.HandlerMapProsPath));
		MapLoader.loadMap(minaPushService, Constants.HandlerMapProsPath);
		log.debug("read configuration from path {}", minaPushService
				.getResources()
				.getResourceName(Constants.AndroidClientProsPath));

		PropertiesUtil propertiesUtil = PropertiesUtil.readProperties(
				minaPushService, Constants.AndroidClientProsPath);
		SERVER_PORT = propertiesUtil.getInteger("serverPort", 18567);
		SERVER_IP = propertiesUtil.getString("serverIp", "localhost");
		log.debug("read configuration SERVER IP  is {},SERVER PORT is {}",
				SERVER_IP, SERVER_PORT);
		if ("localhost".equals(SERVER_IP) || "127.0.0.1".equals(SERVER_IP)) {
			throw new IllegalArgumentException(
					"set your mina server ip please!");
		}

		socketAddress = new InetSocketAddress(SERVER_IP, SERVER_PORT);
		connector = new NioSocketConnector();
		connector.setHandler(this);
		/**
		 * 设置过滤器链
		 */
		DefaultIoFilterChainBuilder chain = connector.getFilterChain();
		// chain.addLast("executor", new ExecutorFilter());
		/**
		 * 设置协议过滤器
		 */
		chain.addLast("codec", new ProtocolCodecFilter(
				new ProtobufCodecFactory()));
		/**
		 * 心跳工厂
		 */
		chain.addLast("keep-alive", new HeartBeatFilter("Mobile Client"));// 心跳
		/**
		 * 并发执行器
		 */
		connectFuture = connector.connect(socketAddress);
		reCreateThread = new Thread(this);
		/**
		 * 读取私钥
		 */
		log.debug("reading private key from path {}", minaPushService
				.getResources().getResourceName(Constants.HandlerMapProsPath));
		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(minaPushService.getResources()
						.openRawResource(R.raw.client_private),
						Charset.forName("utf-8")));
		String readStr = null;
		StringBuilder stringBuilder = new StringBuilder();
		/**
		 * 读取私钥文件
		 */
		try {
			while ((readStr = bufferedReader.readLine()) != null) {
				stringBuilder.append(readStr);
			}
			privateKey = Base64
					.decode(stringBuilder.toString(), Base64.DEFAULT);
			log.debug("reading private key bytes {}",
					Arrays.toString(privateKey));
		} catch (IOException e) {
			log.error("decrypt private key error occur!", e);
		} finally {
			try {
				bufferedReader.close();
			} catch (IOException e) {
				log.error("close io reader error occur!", e);
			}
		}
	}

	/**
	 * [一句话功能简述]<BR>
	 * [功能详细描述]
	 * 
	 * @param ioBuffer
	 * @return
	 * @throws InvalidProtocolBufferException
	 *             2013-6-21 下午1:25:11
	 */
	@Override
	public void exceptionCaught(IoSession session, Throwable cause)
			throws Exception {
		if (cause instanceof SocketException || cause instanceof IOException) {
			this.sessionClosed(session);
		}
		log.error(
				"exception occur in session "
						+ SessionHelper.getSessionId(session), cause);
	}

	/**
	 * [一句话功能简述]<BR>
	 * [功能详细描述]
	 * 
	 * @param ioBuffer
	 * @return
	 * @throws InvalidProtocolBufferException
	 *             2013-6-21 下午1:25:11
	 */
	@Override
	public void messageReceived(IoSession session, Object message)
			throws Exception {
		log.debug("Mobile Session {} messageReceived...",
				SessionHelper.getSessionId(session));
		Packet packet = (Packet) message;
		String fullName = packet.getTypeName();
		AbstractCommandHandler<?> abstractCommandHandler = MapLoader
				.getAbstractCommandHandler(fullName);

		if (abstractCommandHandler != null) {
			abstractCommandHandler.process(session, packet.getPbfBytes()
					.toByteArray());
		}

	}

	/**
	 * [一句话功能简述]<BR>
	 * [功能详细描述]
	 * 
	 * @param ioBuffer
	 * @return
	 * @throws InvalidProtocolBufferException
	 *             2013-6-21 下午1:25:11
	 */
	@Override
	public void messageSent(IoSession session, Object message) throws Exception {
		log.debug("Mobile Session {} messageSent...",
				SessionHelper.getSessionId(session));
	}

	/**
	 * [一句话功能简述]<BR>
	 * [功能详细描述]
	 * 
	 * @param ioBuffer
	 * @return
	 * @throws InvalidProtocolBufferException
	 *             2013-6-21 下午1:25:11
	 */
	@Override
	public void sessionClosed(IoSession session) throws Exception {
		Boolean close_type = (Boolean) session
				.getAttribute(Constants.SAFE_CLOSE);
		doUnregister();
		this.session = null;
		if (close_type == null) {
			log.debug("Mobile Session {} crazy closed,reconnect begin...",
					SessionHelper.getSessionId(session));
			reConnect();
			return;
		}
		// this.connector.dispose();
		log.debug("Mobile Session {} closed...",
				SessionHelper.getSessionId(session));
	}

	public void doUnregister() {

	}

	/**
	 * [一句话功能简述]<BR>
	 * [功能详细描述]
	 * 
	 * @param ioBuffer
	 * @return
	 * @throws InvalidProtocolBufferException
	 *             2013-6-21 下午1:25:11
	 */
	@Override
	public void sessionCreated(IoSession session) throws Exception {
		log.debug("Mobile Session {} created...",
				SessionHelper.getSessionId(session));
	}

	/**
	 * [一句话功能简述]<BR>
	 * [功能详细描述]
	 * 
	 * @param ioBuffer
	 * @return
	 * @throws InvalidProtocolBufferException
	 *             2013-6-21 下午1:25:11
	 */
	@Override
	public void sessionIdle(IoSession session, IdleStatus status)
			throws Exception {
		log.debug("Mobile Session {} idled...",
				SessionHelper.getSessionId(session));
	}

	/**
	 * [一句话功能简述]<BR>
	 * [功能详细描述]
	 * 
	 * @param ioBuffer
	 * @return
	 * @throws InvalidProtocolBufferException
	 *             2013-6-21 下午1:25:11
	 */
	@Override
	public void sessionOpened(IoSession session) throws Exception {
		log.debug("Mobile Session {} opened...",
				SessionHelper.getSessionId(session));
	}

	private byte[] privateKey;

	/**
	 * [私钥]
	 */
	public void start() {

		log.debug("start mina mobile client...");
		new Thread(new Runnable() {

			@Override
			public void run() {
				Boolean keepOpen = SharedPreferencesUtil.getInstance(minaPushService)
						.getBoolean(TmpConstants.SELECT_OPEN, true);
				if(keepOpen){
					submitReq();	
				}
				
			}
		}).start();
	}

	public void sendOnlineBroadCast() {
		EventBus.getEventBus(TmpConstants.EVENTBUS_PUSH, ThreadEnforcer.MAIN)
				.post(new ConnectStatusChangeEvent(
						ConnectStatusChangeEvent.CONN_CHANNEL_MINA,
						ConnectStatusChangeEvent.CONN_STATUS_ONLINE));
	}

	public void sendOfflineBroadCast() {
		EventBus.getEventBus(TmpConstants.EVENTBUS_PUSH, ThreadEnforcer.MAIN)
				.post(new ConnectStatusChangeEvent(
						ConnectStatusChangeEvent.CONN_CHANNEL_MINA,
						ConnectStatusChangeEvent.CONN_STATUS_OFFLINE));
	}

	public void prepairReqConnect() {
		log.debug("prepairReqConnect...");
		new Thread(new Runnable() {

			@Override
			public void run() {
				waiting = 0;
				while (true) {
					try {
						reqQueue.take();
						log.debug("mina take a req connection!");
						try {
							// if (!connector.isActive()) {
							// connector.connect(socketAddress)
							// .awaitUninterruptibly();
							// }
							if (session != null && session.isConnected()) {
								log.debug("session has been connected,take continue...");
								sendOnlineBroadCast();
								continue;
							}
							if (!NetworkUtil
									.isNetworkConnected(minaPushService)) {
//								Toast.makeText(minaPushService, "网络异常，请检查设置！", Toast.LENGTH_SHORT).show();
								sendOfflineBroadCast();
								reqQueue.clear();
								continue;
							}
							log.debug("mina connecting....");
							Auth_Rsp auth_Rsp = null;
							try {
								auth_Rsp = connect("com.foreveross.chameleon",
										privateKey);
								waiting = 0;
								log.debug("mina connect complete....");
								if (auth_Rsp != null) {
									log.debug("mina clear req queue!");
									reqQueue.clear();
								}
							} catch (Exception e) {
								log.error(
										"connect to mina server error,try to reconnect...!",
										e);
								waiting++;
								// 等待至少十秒时间
								try {
									long seconds = (long) waiting() * 1000L;
									log.info("reconnect after {} seconds",
											waiting());
									Thread.sleep(seconds);
								} catch (InterruptedException ex) {
									log.error("Thread sleep error occur!", ex);
								}
								submitReq();
								continue;
							}

							if (minaPushService.getApplication() instanceof SessionIdAware
									&& auth_Rsp != null && auth_Rsp.getSucess()) {
								log.debug("Mina SessionIdAware callback...");
								EventBus.getEventBus(
										TmpConstants.EVENTBUS_PUSH,
										ThreadEnforcer.MAIN).post(auth_Rsp);
								sendOnlineBroadCast();
								SessionIdAware sessionIdAware = SessionIdAware.class
										.cast(minaPushService.getApplication());
								sessionIdAware.sessionIdCreated(
										auth_Rsp.getSessionId(),
										MinaMobileClient.this);
							}

							log.debug("mina connect result is "
									+ (auth_Rsp == null ? false : auth_Rsp
											.getSucess()));
						} catch (Exception e) {
							log.error("connect error occur!", e);
						}
					} catch (InterruptedException e) {
						log.error("take mina connect req error occur!", e);
					}

				}
			}
		}).start();
	}

	public void submitReq() {
		reqQueue.add(0);
	}

	public Auth_Rsp connect(String packageName, byte[] privateKey)
			throws Exception {
		if (!NetworkUtil.isNetworkConnected(minaPushService)) {
			throw new Exception("网络不可用");
		}
		this.privateKey = privateKey;

		if (session != null) {
			log.debug("the previous session {} is not nul,begin close!",
					SessionHelper.getSessionId(session));
			try {
				session.close(true).awaitUninterruptibly();
				log.debug("the previous session {} has been closed!",
						SessionHelper.getSessionId(session));
			} catch (Exception e) {
				log.error("close session {} error occur!",
						SessionHelper.getSessionId(session), e);
			} finally {
				session = null;
			}
		}

		connectFuture = connector.connect(socketAddress);
		connectFuture.awaitUninterruptibly();
		log.debug("connect to mina server successfully!");
		session = connectFuture.getSession();
		log.debug("get session from connection... ");
		return authenticated(packageName, privateKey);
	}

	private Auth_Rsp authenticated(String packageName, byte[] privateKey)
			throws Exception {

		byte[] encodedBytes = RSACoder.encryptByPrivateKey(
				packageName.getBytes("utf-8"), privateKey);

		Auth_Req auth_Req = Auth_Req.newBuilder().setId(IdGenerator.getId())
				.setPackageName(packageName)
				.setEncodedPackageNameBytes(ByteString.copyFrom(encodedBytes))
				.build();
		session.write(auth_Req).awaitUninterruptibly();
		log.debug("auth req has been send to mina server! ");
		final BlockingQueue<Auth_Rsp> blockingQueue = new SynchronousQueue<Auth_Rsp>();
		CallbackStore.instance().setPacketCollector(auth_Req.getId(),
				new PacketCollector<Auth_Rsp>() {

					@Override
					public void onCollect(Auth_Rsp generatedMessage) {
						try {
							blockingQueue.offer(generatedMessage, 10,
									TimeUnit.SECONDS);
							log.debug(
									"Auth_Rsp from {} has been collect result is {}",
									generatedMessage.getIp(),
									generatedMessage.getSucess());
						} catch (InterruptedException e) {
							log.error("blockingQueue offer error occur!", e);
						}

					}
				});
		return blockingQueue.poll(15, TimeUnit.SECONDS);
	}

	public void reConnect() {
		log.debug("Mina mobile client reconnect....");
		// synchronized (MinaMobileClient.class) {
		// reCreateThread = new Thread(this);
		// log.debug("Mina mobile reconnect thread  start....");
		// reCreateThread.start();
		//
		// }
		submitReq();
	}

	public synchronized boolean safeClose() {
		log.debug("Mina mobile client clear req queue....");
		reqQueue.clear();

		log.debug("Mina mobile client safeClose....");
		if (session == null || !session.isConnected()) {
			sendOfflineBroadCast();
			return true;
		}

		session.setAttribute(Constants.SAFE_CLOSE, true);
		try {
			session.close(false);
			session.removeAttribute(Constants.SAFE_CLOSE);
			session = null;
			sendOfflineBroadCast();
			log.debug("Mina mobile client close....");
		} catch (Exception e) {
			log.error("exception occur when session close!", e);
		}

		return true;
	}

	public boolean isConnected() {
		return session != null && session.isConnected();

	}

	/**
	 * [运行UI线程]<BR>
	 * [功能详细描述]
	 * 
	 * @param runnable
	 *            2013-8-8 上午11:58:45
	 */
	public void runOnUi(Runnable runnable) {

		runOnUi(runnable);
	}

	public PacketCollector<Auth_Rsp> getAuthPackageCollector() {
		return new PacketCollector<Auth_Rsp>() {
			@Override
			public void onCollect(final Auth_Rsp auth_Rsp) {
				minaPushService.runOnUi(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(minaPushService,
								"connect result is " + auth_Rsp.getSucess(),
								Toast.LENGTH_SHORT).show();
					}
				});
				if (minaPushService.getApplication() instanceof SessionIdAware
						&& auth_Rsp != null && auth_Rsp.getSucess()) {
					EventBus.getEventBus(TmpConstants.EVENTBUS_PUSH,
							ThreadEnforcer.MAIN).post(auth_Rsp);

					SessionIdAware sessionIdAware = SessionIdAware.class
							.cast(minaPushService.getApplication());
					sessionIdAware.sessionIdCreated(auth_Rsp.getSessionId(),
							MinaMobileClient.this);
				}
			}
		};
	}

	/**
	 * [重连接]<BR>
	 * [功能详细描述] 2013-7-8 下午3:19:12
	 */
	@Override
	public void run() {
		synchronized (this) {
			waiting = 0;
			// 不打断的情况下保持循环
			while (!reCreateThread.isInterrupted()) {
				log.debug("Trying to reconnect mina server in {} seconds",
						waiting());
				// 如果连接是保持的，而且已经验证，则打断跳出循环
				if (session != null) {
					Object authObj = session.getAttribute("auth");
					if (authObj != null && Boolean.class.cast(authObj)
							&& session.isConnected()) {
						log.debug("Trying to reconnect mina server successfully!");
						break;
					}

				}
				// 如果没有连接，或者连接上了没有验证，则连接
				try {
					submitReq();
				} catch (Exception e) {
					// 连接不上或得不到session
					log.error("connect to mina server .error occur...", e);
				}
				// 等待至少十秒时间
				try {
					long seconds = (long) waiting() * 1000L;
					log.info("reconnect after {} seconds", waiting());
					Thread.sleep(seconds);
				} catch (InterruptedException e) {
					log.error("Thread sleep error occur!", e);
				}
				waiting++;
			}
		}
	}

	private int waiting = 0;

	/**
	 * [计算等待时间]<BR>
	 * [功能详细描述]
	 * 
	 * @return 2013-8-8 上午11:58:22
	 */
	private int waiting() {
		if (waiting > 20) {
			return 600;
		}
		if (waiting > 13) {
			return 300;
		}
		return waiting <= 7 ? 10 : 60;
	}

	public boolean online() {
		return session != null && session.isConnected();
	}

}
