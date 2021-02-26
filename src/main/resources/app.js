var app = ( function (){
    function getCss(){
        appClient.getCss("show.css")
    }
    function getJs(){
        appClient.getJs()
    }
    function getImg(){
        appClient.getImg("dogs.jpg")
    }
    function getImg2(){
        appClient.getImg2("cats.png")
    }
    return{
        getJs : getJs,
        getCss: getCss,
        getImg: getCss,
        getImg2: getImg2
    }
})();