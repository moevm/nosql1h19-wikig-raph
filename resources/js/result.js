const urlParams = new URLSearchParams(window.location.search);
let startArticle = urlParams.get("startArticle")
let finishArticle = urlParams.get("endArticle")
let depth = urlParams.get("depth")
let category = urlParams.get("category")
let time = urlParams.get("processfor")
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
     maxEdgeSize: 4,
  }
});
// обработчик кликов,надо будет немного переписать
// Bind the events:
// s.bind('clickNode', (e) => {
//   let neighbors = s.graph.neighborhood(e.data.node.id);
//   console.log(e.data)
//   // s.graph.nodes().forEach(() => {
//   //   mute(this)
//   // })
//   // s.graph.edges.forEach((el) => {
//   //   mute(el);
//   // });
//   // console.log(e.neighbors);
//   // neighbors.nodes
//   if (e.data.captor.ctrlKey)
//     window.open('https://en.wikipedia.org/wiki/'+encodeURIComponent(e.data.node.label), '_blank');
// })
// обработка перемещения вершин
//var dragListener = sigma.plugins.dragNodes(s, s.renderers[0]);