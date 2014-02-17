var treeData = treeData || [];
var tempData = [{
	text:'公共',
	children:[
		{text:"获取司机个人信息",children:[
			{
				text:"输入", nodeType:"input",data:
				{
				    action: "get_driver_info",
				    json:
				    {
				    	"driver_id": 2
					}
				}
			}
		]},
		{text:"获取货主个人信息",children:[
			{
				text:"输入", nodeType:"input",data:
				{
				    action: "get_user_info",
				    json:
				    {
				    	"user_id": 2
					}
				}
			}
		]},
		{text:"获取司机评价列表",children:[
			{
				text:"输入", nodeType:"input",data:
				{
				    action: "get_driver_reply",
				    json:
				    {
				    	"driver_id": 2
					}
				}
			}
		]},
		{text:"获取货主评价列表",children:[
			{
				text:"输入", nodeType:"input",data:
				{
				    action: "get_user_reply",
				    json:
				    {
				    	"user_id": 2
					}
				}
			}
		]},
		{text:"字典数据查询",children:[
			{
				text:"输入", nodeType:"input",data:
				{
				    action: "common_get_dict_list",
				    json:{
				    	"dict_type": "car_type"
				    }
				}
			},
			{
				text:"输出", nodeType:"output",data:{
				    "status": 0,
				    "message": null,
				    "items": [
				    	{
				    		"id":3,
				    		"name": "大货车",
				    		"dict_type": "car_type",
				    		"dict_desc": null
				    	},
				    	{
				    		"id":15,
				    		"name": "火车",
				    		"dict_type": "car_type",
				    		"dict_desc": null
				    	},
				    	{
				    		"id":24,
				    		"name": "玻璃",
				    		"dict_type": "cargo_type",
				    		"dict_desc": "货物类别"
				    	}
				    ]
				}
			}
		]},
		{text:"修改密码",children:[
			{
				text:"输入", nodeType:"input",data:
				{
				    action: "change_password",
				    json:
				    {
				    	"old_password": "111111",
				    	"new_password": "222222"
					}
				}
			}
		]},
		{text:"意见反馈",children:[
			{
				text:"输入", nodeType:"input",data:
				{
				    action: "post_feedback",
				    json:
				    {
				    	"phone": "1333333",
				    	"suggest_content": "搞得不错啊兄弟们"
					}
				}
			}
		]},
		{text:"账户余额",children:[
			{
				text:"输入", nodeType:"input",data:
				{
				    action: "get_account_gold",
				    json:
				    {
					}
				}
			}
		]},
		{text:"信息中心",children:[
			{
				text:"输入", nodeType:"input",data:
				{
				    action: "query_message",
				    json:
				    {
				    	"page": 1,
				    	"page_size": 10
					}
				}
			}
		]},
		{text:"找回密码-获取验证码",children:[
			{
				text:"输入", nodeType:"input",data:
				{
				    action: "get_password_vcode",
				    json:
				    {
				    	"phone": "135"
					}
				}
			}
		]},
		{text:"找回密码",children:[
			{
				text:"输入", nodeType:"input",data:
				{
				    action: "get_password",
				    json:
				    {
				    	"phone": "135",
				    	"vcode": "123456",
				    	"password": "111111",
				    	"user_type": 1
					}
				}
			}
		]}
	]
}];
treeData = treeData.concat(tempData);