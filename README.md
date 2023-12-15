# BusMatch
버스 실시간 도착 정보를 알려주는 앱

## 개요
Bus Match는 실시간으로 버스 도착 정보를 제공하는 안드로이드 애플리케이션입니다. 이 앱은 사용자가 버스 도착 시간을 쉽고 빠르게 확인할 수 있도록 설계되었습니다.

### 개발 환경
개발 언어: Kotlin
최소 지원 API 레벨: 31
타겟 SDK 버전: 33

#### flask, RDS(MySQL) 사용 (localhost)
도전하고 싶은 마음에 kotlin으로 개발했습니다. 그리고 로그인 기능을 위해 firebase를 활용했습니다. 원래는 firestore를 사용하여 버스 id(routeId) 정보를 넣을려고 했습니다. firebase에 데이터를 넣을 때 보통 node.js를 활용합니다. Node.js와 firebase 모두 한 번도 사용해보지 않아서 flask와 RDS를 활용했습니다.

### 활용 데이터
[버스 도착 정보 조회 서비스](https://www.data.go.kr/data/15000314/openapi.do)

<img width="517" alt="image" src="https://github.com/cshooon/BusMatch/assets/113033780/e63156ed-bbd3-40d4-9c34-aae797e0c37a">
<img width="517" alt="image" src="https://github.com/cshooon/BusMatch/assets/113033780/4905ae43-6781-465e-96a8-c74a713243ac">

[서울 버스 노선 조회](https://data.seoul.go.kr/dataList/OA-1095/L/1/datasetView.do)

## UI
<img width="175" height="500" alt="Screenshot 2023-12-15 at 3 04 34 PM" src="https://github.com/cshooon/BusMatch/assets/113033780/b59636c4-d940-4dbb-ab75-6d33a921e40a">
<img width="176" height="500" alt="image" src="https://github.com/cshooon/BusMatch/assets/113033780/bc1868ce-e549-4155-8aad-505a7cc0733e">
<img width="200" height="500" alt="image" src="https://github.com/cshooon/BusMatch/assets/113033780/f3cfa105-313d-4625-8725-1f801fdc6e8a">
<img width="200" height="500" alt="Screenshot 2023-12-15 at 3 53 17 PM" src="https://github.com/cshooon/BusMatch/assets/113033780/53bdf7b0-0665-4ab4-8f24-b645ec8f1bfc">

### 스플래시 스크린 (styles.xml)
앱 시작 시 나타나는 스플래시 스크린입니다.

```xml
    <style name="Theme.SplashEx" parent="Theme.MaterialComponents.DayNight.NoActionBar">
        <item name="android:windowSplashScreenBackground">@color/blue</item>
        <item name="android:windowSplashScreenAnimatedIcon">@drawable/icon</item>
        <item name="android:windowSplashScreenIconBackgroundColor">@color/blue</item>
    </style>

    <style name="Theme.App.Starting" parent="Theme.SplashScreen">
        <item name="windowSplashScreenBackground">@color/blue</item>
        <item name="windowSplashScreenAnimatedIcon">@drawable/icon</item>
        <item name="android:windowSplashScreenBrandingImage">@drawable/logo</item>
        <item name="postSplashScreenTheme">@style/Theme.SplashEx</item>
    </style>
```

수동 구현했던 기존 스플래시와 달리 splash API를 통해 구현합니다.

## 기능 (Strength)
<img width="220" alt="image" src="https://github.com/cshooon/BusMatch/assets/113033780/f5c404d2-9920-42f1-bf64-a92fd1548eda">

검색 기능을 통해 버스 id(routeId), 정류장 id(nodeId)를 몰라도 버스 도착 정보를 조회할 수 있습니다.

## Diffculty

### Login

Login template과 firebase를 이용해 간단히 구현했습니다. Firebase auth 다음과 같이 불러와 사용할 수 있습니다. 로그인에 필요한 로직이 많아 어려움을 겪었고 저는 간단하게 구현했습니다.

### DB
<img width="685" alt="image" src="https://github.com/cshooon/BusMatch/assets/113033780/ed546c67-c2c3-4368-b686-6c5adafe9c05">

위 표 정보를 db에 넣고 flask orm을 통해 검색 기능을 구현했습니다. 버스명을 입력하면 버스 id로 바꾸어주고 이를 통해 api를 호출합니다. http request를 사용할 때 android에서는 코루틴을 사용해야 합니다. 

### Fragment

기존 검색 화면에서 (메인 화면)에서 도착 정보 화면으로 넘어가려면 activity를 바꾸는 방법도 있지만 fragment를 활용하면 간단합니다. 처음에는 scrollView로 구현해서 activity를 바꾸려고 했지만 더 간단한 방법인 fragment를 활용했습니다

## 보완점

1. excel 파일 정보를 firestore에 담는 것입니다.
2. 로그인 정보를 바탕으로 즐겨찾기 기능을 만들고자 합니다.
3. 정류장명으로 검색할 때 버스 id 정보를 버스명으로 변환해서 사용자한테 보여주는 것입니다. 바꾸려고 시도했지만 검색 결과를 담은 searchItem이 http request를 다시 보내면 초기화가 되버립니다. 원인을 찾지 못했습니다.

<img width="613" alt="image" src="https://github.com/cshooon/BusMatch/assets/113033780/31022968-4c49-48cb-abb4-d6af347c433f">


