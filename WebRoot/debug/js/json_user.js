var treeData = treeData || [];
var tempData = [{
	text:'货主端',
	children:[
		{text:"货主注册-获取验证码",children:[
			{
				text:"输入", nodeType:"input",data:
				{
				    action: "user_reg_getcode",
				    json:
				    {
				    	"phone": "135"
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
		{text:"货主注册",children:[
			{
				text:"输入", nodeType:"input",data:
				{
				    action: "user_reg",
				    json:
				    {
				    	"phone": "135",
				    	"vcode": "111111",
				    	"password": "111111",
				    	"device_type": 1
					}
				}
			},
			{
				text:"输出", nodeType:"output",data:{
				    "status": 0,
				    "user_id": 12,
				    "item": {
				    	"token": "e5fc4b775918a8d98808b7dd779cbb6d"
				    }
				}
			}
		]},
		{text:"货主注册-选填",children:[
			{
				text:"输入", nodeType:"input",data:
				{
				    action: "user_reg_optional",
				    json:
				    {
				    	"photo": null,
				    	"recommender_phone": "1355"
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
		{text:"货主登录",children:[
			{
				text:"输入", nodeType:"input",data:
				{
				    action: "userlogin",
				    json:
				    {
				    	"phone": "135",
				    	"password": "111111",
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
		{text:"货源发布",children:[
			{
				text:"输入", nodeType:"input",data:
				{
				    action: "publish_order",
				    json:
				    {
				    	"start_province": 130000,
				    	"start_city": 130100,
				    	"start_district": 130102,
				    	"end_province": 130000,
				    	"end_city": 130200,
				    	"end_district": 130202,
				    	"cargo_desc": "大米",
				    	"cargo_type": 34,
				    	"car_length": 12,
				    	"cargo_weight": 15,
				    	"price": 10.5,
				    	"ship_type": 57,
				    	"car_type": 43,
				    	"cargo_photo1": null,
				    	"cargo_photo2": null,
				    	"cargo_photo3": null,
				    	"loading_time": 1365963574649,
				    	"cargo_remark": null,
				    	"validate_time": 36000000,
				    	"user_bond": 50,
				    	"user_proportion": 0.2
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
		{text:"待定货单列表查询",children:[
			{
				text:"输入", nodeType:"input",data:
				{
				    action: "query_order",
				    json:
				    {
				    	"page": 1,
				    	"page_size": 10
					}
				}
			}
		]},
		{text:"待定货单详细",children:[
			{
				text:"输入", nodeType:"input",data:
				{
				    action: "get_order_detail",
				    json:
				    {
				    	"order_id": 1
					}
				}
			}
		]},
		{text:"待定货单详细-已抢单司机列表",children:[
			{
				text:"输入", nodeType:"input",data:
				{
				    action: "get_order_detail_driver_list",
				    json:
				    {
				    	"order_id": 1
					}
				}
			}
		]},
		{text:"待定货单加价",children:[
			{
				text:"输入", nodeType:"input",data:
				{
				    action: "add_order_price",
				    json:
				    {
				    	"order_id": 1,
				    	"price": 5000
					}
				}
			}
		]},
		{text:"待定货单-取消订单",children:[
			{
				text:"输入", nodeType:"input",data:
				{
				    action: "cancel_order",
				    json:
				    {
				    	"order_id": 15
					}
				}
			}
		]},
		{text:"待定货单-接受抢单",children:[
			{
				text:"输入", nodeType:"input",data:
				{
				    action: "accept_order",
				    json:
				    {
				    	"order_id": 15,
				    	"driver_id": 1
					}
				}
			}
		]},
		{text:"证件验证-身份证查询",children:[
			{
				text:"输入", nodeType:"input",data:
				{
				    action: "get_id_card_info",
				    json:
				    {
				    	"id_card": "611118199902022228",
				    	"user_name": "张三"
					}
				}
			}
		]},
		{text:"证件验证-驾驶证查询",children:[
			{
				text:"输入", nodeType:"input",data:
				{
				    action: "get_license_info",
				    json:
				    {
				    	"license": "611118199902022228",
				    	"user_name": "张三"
					}
				}
			}
		]},
		{text:"证件验证-行驶证查询",children:[
			{
				text:"输入", nodeType:"input",data:
				{
				    action: "get_registration_info",
				    json:
				    {

					}
				}
			}
		]},
		{text:"在途货物查询",children:[
			{
				text:"输入", nodeType:"input",data:
				{
				    action: "query_in_shipping",
				    json:
				    {
				    	"page": 1,
				    	"page_size": 10
					}
				}
			}
		]},
		{text:"定位查询",children:[
			{
				text:"输入", nodeType:"input",data:
				{
				    action: "locate_driver",
				    json:
				    {
				    	"plate_number": "川A12345"
					}
				}
			}
		]},
		{text:"对司机评价",children:[
			{
				text:"输入", nodeType:"input",data:
				{
				    action: "rating_to_driver",
				    json:
				    {
				    	"order_id": 15,
				    	"score1": 5,
				    	"score2": 5,
				    	"score3": 5,
				    	"reply_content": "很好的司机"
					}
				}
			}
		]}
	]
}];
treeData = treeData.concat(tempData);