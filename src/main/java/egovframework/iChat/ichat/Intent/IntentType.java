package egovframework.iChat.ichat.Intent;

public enum IntentType {
	BUTTON("버튼")
	,Shower("샤워실")
	,Relax("릴렉스존")
	,Napzone("냅존")
	,Spa("스파")
	,Book("서점")
	,Tour("여행용품점")
	,FoodCourt("푸드코트")
	,InfoDesk("안내데스크")
	,Cafe("카페")
	,Massage("마사지")
	,Comsmetic("화장품")
	,Fashion("패션")
	,Accessory("액세서리")
	,Souvenir("기념품")
	,Flower("꽃")
	,KoreanFood("한식")
	,WesternFood("양식")
	,AsianFood("아시안")
	,FastFood("패스트푸드")
	,AdminService("민원행정서비스")
	,Internet("인터넷")
	,Snack("스낵")
	,Dessert("디저트")
	,CommonTalk("일상대화")
	;
	
	final private String name;
	
	private IntentType(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
}
