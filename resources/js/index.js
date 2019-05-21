 let host = "https://api."+window.location.hostname;
//let host = "http://localhost:1337";
function onLoadPage(){

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

function exportDB() {
    alert("export")
    let filename = document.getElementById('exportFilename').value.trim()
    if( filename == null || filename == "")
    {
        alert("enter filename for export")
    }
    else
    {
        var xhr = new XMLHttpRequest();
        xhr.open('GET', host + '/exportToFile?filename=' + filename, false);
        xhr.send();
        if (xhr.status != 200) {
          console.log( xhr.status + ': ' + xhr.statusText ); // пример вывода: 404: Not Found
        } else {
          $('#exportModal').modal('hide')
        }
    }
}

function importDB() {
    let filename = document.getElementById('importFilename').value.trim()
    if( filename == null || filename == "")
        alert("enter filename for import")
    else
    {
        var xhr = new XMLHttpRequest();
        xhr.open('GET', host + '/importFromFile?filename='+filename, false);
        xhr.send();
        if (xhr.status != 200) {
          console.log( xhr.status + ': ' + xhr.statusText ); // пример вывода: 404: Not Found
          alert('error')
        } else {
          $('#importModal').modal('hide')
        }
    }
}