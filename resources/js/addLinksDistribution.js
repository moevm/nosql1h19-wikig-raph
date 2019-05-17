function loadDistribution(){
    // let host = "https://api."+window.location.hostname;
    let host = "http://localhost:1337";
    const urlParams = new URLSearchParams(window.location.search);
    let type = urlParams.get("type")
    let log = true;
    var xhr = new XMLHttpRequest();
    if (type === 'incoming')
        xhr.open('GET', host + '/incomingRelations', false);
    else if (type === 'outgoing')
        xhr.open('GET', host + '/outgoingRelations', false);
    else {
        console.log( 'incorrect type' );
        log = false;
    }
    if(log){
        xhr.send();
        if (xhr.status != 200) {
          console.log( xhr.status + ': ' + xhr.statusText ); // пример вывода: 404: Not Found
        } else {
          drawTable( JSON.parse(xhr.responseText))
        }
    }
}

function drawTable(response){
    if (Array.isArray(response) && response.length)
    {
        let container = document.getElementById('visualization');
        let items = [];

        let table_head = document.getElementById('table_head');
        let hRow = document.createElement("tr");
        for (let key in response[0]) {
              let th = document.createElement("th");

              th.innerHTML = key;
              hRow.appendChild(th);
        }
        table_head.appendChild(hRow)
        let table_body = document.getElementById('table_body');
        for (let id_row in response) {
            let item = {};
            let bRow = document.createElement("tr");
            for (let id_col in response[id_row]) {
                if (id_col == 'Count of vertexes')
                {
                    item.y = Math.log10(parseInt(response[id_row][id_col])+1);
                    item.group = 1;
                }
                else
                {
                    item.x = parseInt(response[id_row][id_col]);
                }
                let td = document.createElement("td");
                td.innerHTML = response[id_row][id_col];
                bRow.appendChild(td);
            }
            items.push(item)
            table_body.appendChild(bRow)
        }
        console.log(items);
        let dataset = new vis.DataSet(items);
        console.log(items[0].x, items[items.length-1].x)
        var groups = new vis.DataSet();
        groups.add({
            id: 1,
            content: "statistic",
            options: {drawPoints: false}
        });
        var options = {
            start: -20,
            end: items[items.length-1].x+20,
            showMajorLabels:false,

        };
        var graph2d = new vis.Graph2d(container, dataset, groups, options);

    }

}