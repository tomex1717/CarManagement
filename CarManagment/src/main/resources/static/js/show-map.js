var map;
var directionsString = directionsStringTl;

var jsonVar = JSON.parse(directionsString);

function initMap() {

    map = new google.maps.Map(document.getElementById("map"), {

        center: {lat: jsonVar[0].lat, lng: jsonVar[0].lng},
        zoom: 12
    });

    var routePath = new google.maps.Polyline({
        path: jsonVar,
        geodesic: true,
        strokeColor: '#FF0000',
        strokeOpacity: 1.0,
        strokeWeight: 2
    });
    routePath.setMap(map);

}


var slider = document.getElementById("myRange");
var output = document.getElementById("hourIndicator");
output.innerHTML = '00' + ':' + '00' // Display the default slider value

// Update the current slider value (each time you drag the slider handle)
slider.oninput = function () {
    var hours = Math.floor(slider.value / 60);
    var minutes = slider.value - (hours * 60);

    if (hours.toString().length == 1) hours = '0' + hours;
    if (minutes.toString().length == 1) minutes = '0' + minutes;


    output.innerHTML = hours + ':' + minutes;


};



