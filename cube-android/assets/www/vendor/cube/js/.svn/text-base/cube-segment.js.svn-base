/*
 * Html页面重，引用segment组件。
 * <segment id="seg1" 
 *    type="radio"              //按钮组的类型，包括 radio 与 checkbox
 *    name="name-seg1"          //组件的名称，触发change事件后会以此名字返回选中直。如: {name-seg1 : 1111}
 *    >
 *      <button value="1111" class="active">1111</button>
 *      <button value="2222">2222</button>
 * </segment>
 *
 * JS中加入事件
 *  var seg1 = new Segment({
 *      autoload : true,      //初始化时是否自动促发按钮事件
 *      selector : "seg1",    //目标组件的ID
 *      change : function(clickbtn, values) {    //chenge事件。可无！
 *          alert("Seg1: " + values);
 *      }
 *  });
 *
 * 按钮组组件，最终转换出html5
 * <div id="seg1" class="btn-group" data-toggle="buttons-radio" name="name-seg1">
 *      <button value="1" class="btn active">1</button>
 *      <button value="1" class="btn">1</button>
 * </div>
 */
define(['jquery', 'underscore', 'bootstrap'], function($, _, bt){
    
    //判断是否已替换，来判断是否已经构造完成
    var Segment = function(config){
        //console.log("Cube:Segment.init(" + config.selector + ")");
        this.config = {
            autoload : true
        };
        this.config = _.extend(this.config, config);
        this.jqObject = $("#" + config.selector);
        this.config.name = $(this.jqObject).attr('name');
        this.config.type = $(this.jqObject).attr('type');
        this.config.inputId = config.selector + "_" + this.config.name;

        //组件change事件
        var me = this;
        $(this.jqObject).find("button").click(function(){
            me.change(this);
        });
        this.listener = config.change;

        //渲染组件
        if (!this.rendered()) {this.render()};

        //初始化时促发按钮change事件。
        if(config.autoload) {
            var selectBtns = $(this.jqObject).find("button.active");
            if(selectBtns && selectBtns.length>0) {
                this.change(null);
            }
        }
    };

    //获取组件配置参数
    Segment.prototype.param = function(key) {
        return this.jqObject.attr(key);
    };

    //根据elememt来获取当前选中的值。
    //  如果elememt是空，则是初始化时候要获取当前选中值，所以要选取全部class为active的按钮；
    //  如果element不是空的，则事件是由人为促发，需要计算active状态切换后的选中情况。
    Segment.prototype.values = function(clickBtn) {
        //1. 如果是radio buttons 而且是人为点击，直接返回被点击的按钮的值。
        if(this.config.type == "radio" && clickBtn) {
            return $(clickBtn).val();
        }

        //获取当前所有选中的值
        var selectBtns = $(this.jqObject).find("button.active");
        var values = new Array();
        if(selectBtns && selectBtns.length > 0) {
            selectBtns.each(function(){
                values.push($(this).val());
            });
        }

        //2. 如果是非人为点击，直接返回被点击的按钮的值。
        if(!clickBtn) { return values; }

        var result = new Array();
        var clicked = false;                    //
        var clickVal = $(clickBtn).val();       //
        //3. 如果是checkbox buttons 而且是人为点击，则需要计算点击后的返回值。
        for (var i = values.length - 1; i >= 0; i--) {
            if(values[i] == clickVal) {
                clicked = true;
            } else {
                result.push(values[i]);
            }
        };
        if(!clicked) {
            result = values;
            result.push(clickVal);
        }

        return result;
    }

    //此组件是否已渲染
    Segment.prototype.rendered = function() {
        //console.log("Rendered:" + (this.jqObject.get(0).nodeName == "DIV"));
        return this.jqObject.get(0).nodeName == "DIV";
    };

    //渲染组件
    Segment.prototype.render = function() {
        //console.log("Cube:Segment.render");
        this.config['id'] = this.param('id');
        
        //构造按钮组组件html
        var segment = this.jqObject;
        var buttonGroup = $("<div/>").attr('id', this.config['id']);
        buttonGroup.addClass('btn-group');
        var btnGrpType = $(segment).attr('type');
        if(btnGrpType) buttonGroup.attr('data-toggle', ('buttons-'+btnGrpType));
        buttonGroup.attr('style', $(segment).attr('style'));
        buttonGroup.attr('name', this.config.name);

        //加入按钮
        var buttons = this.jqObject.children();
        buttons.each(function(){
            var button = $(this);
            button.addClass('btn');
            button.appendTo(buttonGroup);
        });

        //替换组件
        buttonGroup.insertBefore(this.jqObject);
        this.jqObject.remove();

        this.jqObject = buttonGroup;

        //增加隐藏值，以装载组件的值
        var input = $("<input/>").attr('id', this.config.inputId).attr('type', 'hidden').attr('name', this.config.name).val(this.values(null));
        buttonGroup.before(input);
    };

    //change事件需要执行用户自定义事件，还要广播事件。
    Segment.prototype.change = function(element) {
        var values = this.values(element);
        $("#"+this.config.inputId).val(values);
        if(this.listener) {
            this.listener(element, values);
        }
        var name = this.config.name;
        // var param = {name : values};
        var paramStr = "({'" + name + "' : '" + values + "'})";
        var param = eval(paramStr);
        // alert(paramStr);
        $(this.jqObject).trigger('change', param);
    };

    return Segment;
});