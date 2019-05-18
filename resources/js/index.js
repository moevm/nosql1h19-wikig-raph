function onLoadPage(){
    // let host = "https://api."+window.location.hostname;
    let host = "http://localhost:1337";
    articleCount = document.getElementById('articleCount')
    var xhr = new XMLHttpRequest();
    xhr.open('GET', host + '/countOfArticles', false);
    xhr.send();
    if (xhr.status != 200) {
      console.log( xhr.status + ': ' + xhr.statusText ); // пример вывода: 404: Not Found
    } else {
      articleCount.innerHTML = xhr.responseText;
    }
}