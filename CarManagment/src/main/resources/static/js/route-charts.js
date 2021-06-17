var labels = [];
var dataY = [];

function makeDataForChart(routesJson) {
    let chartDataToReturn = [];

    for (let i = 0; i < routesJson.length; i++) {
        for (let j = 0; j < routesJson[i].length; j++) {

            let date = new Date(routesJson[i][j].timestamp);
            let x = date.getHours() + ":" + (date.getMinutes() < 10 ? '0' : '') + date.getMinutes() + ":" + (date.getSeconds() < 10 ? '0' : '') + date.getSeconds();
            let y = routesJson[i][j].speed;
            labels.push(x);
            dataY.push(y);
            chartDataToReturn.push({x: x, y: y});
        }
    }
    return chartDataToReturn;
}

function drawSpeedChart(routesJson) {
    var chartData = makeDataForChart(routesJson);

    // new Chart(
    //     document.getElementById("speedChart"),
    //     {
    //         type: 'line',
    //         data: chartData,
    //         options: {
    //             scales: {
    //                 x: {
    //                     type: 'linear',
    //                     position: 'bottom'
    //
    //                 },
    //                 y: {
    //
    //                    beginAtZero: true
    //                 }
    //             },
    //             plugins:{
    //
    //             }
    //
    //         }
    //     }
    // );

    new Chart(document.getElementById("speedChart"), {
        type: 'bar',
        data: {
            labels: labels,
            datasets: [
                {
                    label: "KM/H",
                    backgroundColor: ["#3e95cd"],
                    data: dataY
                }
            ]
        },
        options: {
            legend: {display: false},
            title: {
                display: true,
                text: 'Prędkość pojazdu',


            },
            responsive: true,
            // layout: {
            //   padding: 40
            // },

            plugins: {
                title: {
                    display: true,
                    text: "Prędkość pojazdu"
                },
                zoom: {
                    zoom: {
                        wheel: {
                            enabled: true,
                        },
                        pinch: {
                            enabled: true
                        },

                        mode: 'x',
                    },

                    pan: {
                        mode: 'x',
                        enabled: true
                    },
                }
            }
        }
    });


}
