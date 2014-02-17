var treeData = treeData || [];
var tempData = [{
	text:'司机端',
	children:[
		{text:"司机注册-获取验证码",children:[
			{
				text:"输入", nodeType:"input",data:
				{
				    action: "driver_reg_getcode",
				    json:
				    {
				    	"phone": "136"
					}
				}
			},
			{
				text:"输出", nodeType:"output",data:{
				    "status": 0,
				    "message": null
				}
			}
		]},
		{text:"司机注册",children:[
			{
				text:"输入", nodeType:"input",data:
				{
				    action: "driver_reg",
				    json:
				    {
				    	"phone": "136",
				    	"vcode": "111111",
				    	"password": "96e79218965eb72c92a549dd5a330112",
				    	"name": "李司机",
				    	"recommender": "135",
				    	"car_length": 12,
				    	"car_type": 19,
				    	"car_weight": 15,
				    	"plate_number": "川A594SB",
				    	"start_province": 10010,
				    	"start_city": 110101,
				    	"end_province": 130000,
				    	"end_city": 130100,
				    	"device_type": 1
					}
				}
			},
			{
				text:"输出", nodeType:"output",data:{
				    "status": 0,
				    "item": {
				    	"driver_id": 12,
				    	"token": "e5fc4b775918a8d98808b7dd779cbb6d"
				    }
				}
			}
		]},
		{text:"司机注册-选填",children:[
			{
				text:"输入", nodeType:"input",data:
				{
				    action: "driver_reg_optional",
				    json:
				    {
				    	"driver_name": "李司机",
				    	"owner_phone": "186",
				    	"id_card": "1355",
				    	"id_card_name": "张三",
				    	"id_card_photo": null,
				    	"registration": "1355",
				    	"registration_name": "李车主",
				    	"registration_photo": null,
				    	"license": "1355",
				    	"license_name": "张三",
				    	"license_photo": null,
				    	"car_photo1": null,
				    	"car_photo2": null,
				    	"car_photo3": null
					}
				}
			},
			{
				text:"输出", nodeType:"output",data:{
				    "status": 0,
				    "message": null
				}
			}
		]},
		{text:"司机登录",children:[
			{
				text:"输入", nodeType:"input",data:
				{
				    action: "driverlogin",
				    json:
				    {
				    	"phone": "136",
				    	"password": "96e79218965eb72c92a549dd5a330112",
				    	"device_type": 1
					}
				}
			},
			{
				text:"输出", nodeType:"output",data:{
				    "status": 0,
				    "message": null,
				    "item": {
				    	"token": "e5fc4b775918a8d98808b7dd779cbb6d"
				    }
				}
			}
		]},
		{text:"新货源列表查询",children:[
			{
				text:"输入", nodeType:"input",data:
				{
				    action: "query_source_order",
				    json:
				    {
				    	"page": 1,
				    	"page_size": 10
					}
				}
			},
			{
				text:"输出", nodeType:"output",data:{
				    "status": 0,
				    "message": null
				}
			}
		]},
		{text:"新货源详情查询",children:[
			{
				text:"输入", nodeType:"input",data:
				{
				    action: "get_source_order_detail",
				    json:
				    {
				    	"order_id": 1
					}
				}
			}
		]},
		{text:"新货源-抢单",children:[
			{
				text:"输入", nodeType:"input",data:
				{
				    action: "place_source_order",
				    json:
				    {
				    	"order_id": 1,
				    	"driver_bond": 100.0
					}
				}
			}
		]},
		{text:"新货源-关注",children:[
			{
				text:"输入", nodeType:"input",data:
				{
				    action: "attention_source_order",
				    json:
				    {
				    	"order_id": 1
					}
				}
			}
		]},
		{text:"待定货源列表查询",children:[
			{
				text:"输入", nodeType:"input",data:
				{
				    action: "query_pending_source_order",
				    json:
				    {
				    	"page": 1,
				    	"page_size": 10
					}
				}
			}
		]},
		{text:"待定货源-取消抢单",children:[
			{
				text:"输入", nodeType:"input",data:
				{
				    action: "cancel_place_source_order",
				    json:
				    {
				    	"order_id": 1
					}
				}
			}
		]},
		{text:"在途货源查询",children:[
			{
				text:"输入", nodeType:"input",data:
				{
				    action: "query_pending_source_order_in_shipping",
				    json:
				    {
				    	"page": 1,
				    	"page_size": 10
					}
				}
			}
		]},
		{text:"在途货源-状态变更",children:[
			{
				text:"输入", nodeType:"input",data:
				{
				    action: "shipping_order_update_status",
				    json:
				    {
				    	"order_id": 1,
				    	"status": 2
					}
				}
			}
		]},
		{text:"在途货源-位置汇报",children:[
			{
				text:"输入", nodeType:"input",data:
				{
				    action: "shipping_order_update_location",
				    json:
				    {
				    	"order_id": 1,
				    	"location": "北京市东城区",
				    	"longitude": 116.23,
				    	"latitude": 39.54
					}
				}
			}
		]},
		{text:"对货主评价",children:[
			{
				text:"输入", nodeType:"input",data:
				{
				    action: "rating_to_user",
				    json:
				    {
				    	"order_id": 1,
				    	"score1": 5,
				    	"score2": 5,
				    	"score3": 5,
				    	"reply_content": "相当不错哦"
					}
				}
			}
		]},
		{text:"我的ABC信息查询",children:[
			{
				text:"输入", nodeType:"input",data:
				{
				    action: "driver_profile",
				    json:
				    {
					}
				}
			}
		]},
		{text:"回单密码验证",children:[
			{
				text:"输入", nodeType:"input",data:
				{
				    action: "validate_receipt_password",
				    json:
				    {
				    	"order_id": 1,
				    	"receipt_password": "abc"
					}
				}
			}
		]},
		{text:"已完成货源查询",children:[
			{
				text:"输入", nodeType:"input",data:
				{
				    action: "driver_query_finished_order",
				    json:
				    {
				    	"page": 1,
				    	"page_size": 10
					}
				}
			}
		]}
	]
}];
treeData = treeData.concat(tempData);