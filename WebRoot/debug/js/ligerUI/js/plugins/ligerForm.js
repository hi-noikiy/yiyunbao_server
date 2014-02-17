/**
* jQuery ligerUI 1.1.7
* 
* Author leoxie [ gd_star@163.com ] 
* 
*/

(function ($)
{

    $.ligerDefaults = $.ligerDefaults || {};
    $.ligerDefaults.Form = {
        width: null,
        //文本框的统一宽度
        inputWidth: null
    };

    $.fn.ligerForm = function (options)
    {
        return this.each(function ()
        {
            var p = $.extend({}, $.ligerDefaults.Form, options || {});
            $("input,select", this).each(function ()
            {
                var jinput = $(this);
                var inputOptions = {};
                if (p.inputWidth)
                    inputOptions.width = p.inputWidth;
                if (jinput.is("select"))
                {
                    jinput.ligerComboBox(inputOptions);
                }
                else if (jinput.is(":text") || jinput.is(":password"))
                { 
                    var ltype = jinput.attr("ltype");
                    switch (ltype)
                    {
                        case "select":
                            jinput.ligerComboBox(inputOptions);
                            break;
                        case "spinner":
                            jinput.ligerSpinner(inputOptions);
                            break;
                        case "date":
                            jinput.ligerDateEditor(inputOptions);
                            break;
                        default:
                            jinput.ligerTextBox(inputOptions);
                            break;
                    }
                }
                else if (jinput.is(":radio"))
                {
                    jinput.ligerRadio(inputOptions);
                }
                else if (jinput.is(":checkbox"))
                {
                    jinput.ligerCheckBox(inputOptions);
                }
            });
        });
    };

})(jQuery);