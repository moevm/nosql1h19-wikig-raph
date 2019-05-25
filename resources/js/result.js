const urlParams = new URLSearchParams(window.location.search);
let startArticle = urlParams.get("startArticle")
let finishArticle = urlParams.get("endArticle")
let depth = urlParams.get("depth")
let category = urlParams.get("category")
let time = urlParams.get("processfor")
let NODES = null;
if(time === null)
  time = 10
//let host = "https://api."+window.location.hostname;
let host = "http://localhost:1337";
let api_link = undefined;

if (finishArticle)
{
    api_link = `${host}/allShortestPaths?startArticle=${startArticle}&finishArticle=${finishArticle}&depth=${depth}&processfor=${time}`
}
else if (category)
{
    api_link = `${host}/articlesOfCategory?category=${category}&processfor=${time}`
}
else if (depth)
 {
     api_link = `${host}/linkedarticles?startArticle=${startArticle}&depth=${depth}&processfor=${time}`
 }
console.log(api_link)


sigma.classes.graph.addMethod('neighbors', function(nodeId) {
    var k,
        neighbors = {},
        index = this.allNeighborsIndex[nodeId] || {};

    for (k in index) {
        neighbors[k] = this.nodesIndex[k];
    }
    return neighbors;
});


/*function mute(node) {
console.log(node);
    node.setAttributeNS(null, 'class', node.getAttribute('class') + ' muted');
}

function unmute(node) {
  node.setAttributeNS(null, 'class', node.getAttribute('class').replace(/(\s|^)muted(\s|$)/g, '$2'));
}

$('.sigma-node').click(function() {

  // Muting
  $('.sigma-node, .sigma-edge').each(function() {
    mute(this);
  });

  // Unmuting neighbors
  var neighbors = s.graph.neighborhood($(this).attr('data-node-id'));
  neighbors.nodes.forEach(function(node) {
    unmute($('[data-node-id="' + node.id + '"]')[0]);
  });

  neighbors.edges.forEach(function(edge) {
    unmute($('[data-edge-id="' + edge.id + '"]')[0]);
  });
});*/


sigma.parsers.json(api_link, {
  renderer: {
    container: document.getElementById('graph-container'),
    type: 'canvas'
  },
   settings: {
     doubleClickEnabled: false,
     // hideNodesOnMove: true,
     hideEdgesOnMove: true,
     minEdgeSize: 0.5,
     maxEdgeSize: 1,
  }
},
  function(s) {
  // Initialize the dragNodes plugin:
  var dragListener = sigma.plugins.dragNodes(s, s.renderers[0]);
  let count_of_articles = s.graph.nodes().length;
//  function onclickSearch(){
//      searchNodeByLabel(s);
//       alert("da");
//       return false;
//  }
  let xhr = new XMLHttpRequest();
  xhr.open('GET', host + '/countOfArticles', false);
  xhr.send();
  if (xhr.status != 200) {
    console.log( xhr.status + ': ' + xhr.statusText ); // пример вывода: 404: Not Found
  } else {
    let db_size = parseInt(xhr.responseText);
    document.getElementById('count_of_articles').innerHTML = count_of_articles;
    document.getElementById('coverage_percentage').innerHTML = Math.round(count_of_articles*100000/db_size)/1000;
  }
//  dragListener.bind('startdrag', function(event) {
////    console.log(event);
//  });
//  dragListener.bind('drag', function(event) {
////    console.log(event);
//  });
//  dragListener.bind('drop', function(event) {
////    console.log(event);
//  });
//  dragListener.bind('dragend', function(event) {
//  });
          NODES = s.graph.nodes()
            s.graph.edges().forEach(function(e) {
                e.color = "rgba("+Math.floor(Math.random()*255)+","+Math.floor(Math.random()*255)+","+Math.floor(Math.random()*255)+", 0.52)";
                e.type = "arrow"
            });

          s.graph.nodes().forEach(function(n) {
                   n.color = "rgb("+Math.floor(Math.random()*255)+","+Math.floor(Math.random()*255)+","+Math.floor(Math.random()*255)+")";
                   });
                  // обработчик кликов,надо будет немного переписать
                  // Bind the events:
                   s.bind('clickNode', (e) => {
                     document.getElementById("relatedNodes").innerHTML = " ";
                     let neighbors = s.graph.neighbors(e.data.node.id);
                     //console.log(neighbors);
                     //console.log(e.data);
                     for (n in neighbors){
                     //console.log(neighbors[n]);
                        document.getElementById("relatedNodes").innerHTML += "<li><a href='https://en.wikipedia.org/wiki/" + neighbors[n].label + "'>" + neighbors[n].label + "</a></li>";
                     }
                    /* console.log(s.graph);
                     for (let i = 0; i < neighbors.length; i++){
                        mute(neighbors[i]);

                     }
                      s.graph.edges.forEach((el) => {
                        mute(el);
                      });*/

                      //ctrl+лкм переход на википедию
                     if (e.data.captor.ctrlKey)
                       window.open('https://en.wikipedia.org/wiki/'+encodeURIComponent(e.data.node.label), '_blank');
                   });
              s.refresh();
      }
);


//function searchNodeByLabel(s){
//    let label = document.getElementById("inlineFormInput").value;
//    s.graph.nodes().forEach(function(n) {
//        if (n.label === label){
//        alert("DA");
//        return false;
//            //document.getElementById("inlineFormInput").value = "НАШЛАСЬ!";
//            //return n;
//        }
//    });
//    alert("NET");
//    return false;
//}
// обработка перемещения вершин
//var dragListener = sigma.plugins.dragNodes(s, s.renderers[0]);
