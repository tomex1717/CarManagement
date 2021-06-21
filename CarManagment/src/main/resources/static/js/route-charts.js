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
            chartDataToReturn.push(routesJson[i][j]);

        }
    }
    return chartDataToReturn;
}


function drawSpeedChart(routesJson) {
    var chartData = makeDataForChart(routesJson);

    var chart = new Chart(document.getElementById("speedChart"), {
        type: 'bar',
        data: {
            labels: labels,
            datasets: [
                {
                    label: "KM/H",
                    backgroundColor: ["#cd3e3e"],
                    data: dataY
                }
            ]
        },
        options: {


            onHover: function (e, item, test, test2) {

                const canvasPosition = Chart.helpers.getRelativePosition(e, chart);

                // Substitute the appropriate scale IDs
                const dataX = chart.scales.x.getValueForPixel(canvasPosition.x);


                const latlng = {
                    lat: chartData[dataX].lat,

                    lng: chartData[dataX].lng,
                };


                map.setCenter(latlng);
                // var positionMarker = new google.maps.Marker({
                //     position: latlng,
                //     icon: car_icon,
                //
                //     map: map
                //
                //
                // });
            },


            legend: {display: false},
            title: {
                display: true,
                text: 'Prędkość pojazdu',


            },
            responsive: true,


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
