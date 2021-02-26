var appClient = (function (){

    function getCss(name){

        $.get("http://localhost:36000/"+name, function(){} 'json');
    }

    function getImg(name){
        $.get("http://localhost:36000/"+name, function(){} 'json');

    function getJs(name){
        $.get("http://localhost:36000/"+name, function(){} 'json');

    function getImg2(name){
        $.get("http://localhost:36000/"+name, function(){} 'json');

    return {
            getJs : getJs,
            getCss: getCss,
            getImg: getImg,
            getImg2: getImg2
        };

})();