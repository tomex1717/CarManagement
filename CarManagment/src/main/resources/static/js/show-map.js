var map;
var directionsString = directionsStringTl;

if (directionsStringTl) {
    document.getElementById("noReportsMessage").innerText = "Brak raportów dla danego dnia, wybierz inny dzień";
}
var jsonVar = JSON.parse(directionsString);

var carPostitionMarker = "/images/carMarker.svg";

function initMap() {

    map = new google.maps.Map(document.getElementById("map"), {

        center: {
            lat: jsonVar[(Object.keys(jsonVar).length - 1)][(Object.keys(jsonVar[(Object.keys(jsonVar).length - 1)]).length - 1)].lat,
            lng: jsonVar[(Object.keys(jsonVar).length - 1)][(Object.keys(jsonVar[(Object.keys(jsonVar).length - 1)]).length - 1)].lng
        },
        zoom: 16
    });

    //color palette for route drawing, explicit since random color sometimes were almost same
    // and it was hard to distinguish routes. Tried to pick up contrast colors
    var colorPalette = ["#ff0000", "#ffff00", "#00ff00", "#080808", "#0000FF", "#800080", "#1ABC9C"
        , "#F2D7D5", "#DC7633", "#A3E4D7", "#717D7E", "#641E16", "#A3E4D7", "#F9E79F", "#EAECEE", "#6E2C00", "#D6EAF8", "#17202A",
        "#ff0000", "#ffff00", "#00ff00", "#080808", "#0000FF", "#800080", "#1ABC9C"
        , "#F2D7D5", "#DC7633", "#A3E4D7", "#717D7E", "#641E16", "#A3E4D7", "#F9E79F", "#EAECEE", "#6E2C00", "#D6EAF8", "#17202A",
        "#ff0000", "#ffff00", "#00ff00", "#080808", "#0000FF", "#800080", "#1ABC9C"
        , "#F2D7D5", "#DC7633", "#A3E4D7", "#717D7E", "#641E16", "#A3E4D7", "#F9E79F", "#EAECEE", "#6E2C00", "#D6EAF8", "#17202A"];


    // Polyline on map

    const geocoder = new google.maps.Geocoder();

    for (let i = 0; i < Object.keys(jsonVar).length; i++) {

        var routePath = new google.maps.Polyline({
            path: jsonVar[i],

            geodesic: true,
            // strokeColor: "#"+((1<<24)*Math.random()|0).toString(16),
            strokeColor: colorPalette[i],
            strokeOpacity: 1.0,
            strokeWeight: 5,
            icons: [{
                icon: {path: google.maps.SymbolPath.FORWARD_CLOSED_ARROW},
                offset: '100%'
            }]


        });
        routePath.setMap(map);


        // infowindow with start/end address and time


        function msToTime(duration) {

            var seconds = Math.floor((duration / 1000) % 60),
                minutes = Math.floor((duration / (1000 * 60)) % 60),
                hours = Math.floor((duration / (1000 * 60 * 60)) % 24);

            hours = (hours < 10) ? "0" + hours : hours;
            minutes = (minutes < 10) ? "0" + minutes : minutes;
            seconds = (seconds < 10) ? "0" + seconds : seconds;

            return hours + ":" + minutes + ":" + seconds;
        }


        google.maps.event.addListener(routePath, 'click', function (event) {
                var startRoute = jsonVar[i][0];
                var endRoute = jsonVar[i][jsonVar[i].length - 1];

                var startEndRoute = [startRoute, endRoute];
                var addresses = [];


                // convert millis difference between end and start to time (travel time)
                var timestamp = endRoute.timestamp - startRoute.timestamp;
                var time = msToTime(timestamp);


                // function geocode() {
                //     for (let i= 0; i < startEndRoute.length; i++){
                //         let result0;
                //         // potrzebuje  poczekac az wykona ta petle i wypelni addresses[].
                //         geocoder.geocode({location: startEndRoute[i]}, (results, status) => {
                //             if (status === "OK") {
                //                 if (results[0]) {
                //                     // console.log(results[0]);
                //                     //wypelnianie
                //                     if(i<1) {
                //                         result0 = results[0].formatted_address;
                //                     }
                //
                //                     if (i === 1 ){
                //                         infoWindow(result0,results[0].formatted_address);
                //                     }
                //
                //                 } else {
                //                     window.alert("No results found");
                //                 }
                //             } else {
                //                 window.alert("Geocoder failed due to: " + status);
                //             }
                //
                //         });
                //     }
                //
                //
                //
                // }

                function geocode() {
                    var res0 = null
                    var res1 = null
                    geocoder.geocode({location: startEndRoute[0]}, (results, status) => {
                        if (status === "OK") {
                            if (results[0]) {
                                res0 = results[0].formatted_address;
                                if (res0 && res1) infoWindow(res0, res1);
                            } else {
                                window.alert("No results found");
                            }
                        } else {
                            window.alert("Geocoder failed due to: " + status);
                        }
                    });
                    geocoder.geocode({location: startEndRoute[1]}, (results, status) => {
                        if (status === "OK") {
                            if (results[0]) {
                                res1 = results[0].formatted_address;
                                if (res0 && res1) infoWindow(res0, res1);
                            } else {
                                window.alert("No results found");
                            }
                        } else {
                            window.alert("Geocoder failed due to: " + status);
                        }
                    });
                }


                function infoWindow(result, result1) {
                    var toolTip = '<div id="map-box">' +
                        '<div id="siteNotice">' +
                        '</div>' +
                        '<div class="infowindow-header">Początek:</div>' +
                        '<div class="infowindow-data">' + result +
                        '<br>' + '<div class="infowindow-date">godz:  ' + new Date(startRoute.timestamp).toLocaleTimeString() + '</div></div>' +
                        '<div class="infowindow-header">Koniec:</div>' +
                        '<div class="infowindow-data">' + result1 +
                        '<br>' + '<div class="infowindow-date">godz:' + new Date(endRoute.timestamp).toLocaleTimeString() + '</div>' +
                        '<div class="infowindow-header">Czas podróży:</div>' +
                        '<div class="infowindow-data">' + time + '</div>' +

                        '</div>';

                    var infowindow = new google.maps.InfoWindow({
                        map: map
                    });

                    infowindow.setContent(toolTip);
                    infowindow.setPosition(event.latLng);
                    infowindow.open(map);

                }

                geocode();


            }
        );

    }


// CAR POSITION ON MAP
    var anchor = new google.maps.Point(150, 382);

// car icon
    var car_icon = {
        path: "M499.99 176h-59.87l-16.64-41.6C406.38 91.63 365.57 64 319.5 64h-127c-46.06 0-86.88 27.63-103.99 70.4L71.87 176H12.01C4.2 176-1.53 183.34.37 190.91l6 24C7.7 220.25 12.5 224 18.01 224h20.07C24.65 235.73 16 252.78 16 272v48c0 16.12 6.16 30.67 16 41.93V416c0 17.67 14.33 32 32 32h32c17.67 0 32-14.33 32-32v-32h256v32c0 17.67 14.33 32 32 32h32c17.67 0 32-14.33 32-32v-54.07c9.84-11.25 16-25.8 16-41.93v-48c0-19.22-8.65-36.27-22.07-48H494c5.51 0 10.31-3.75 11.64-9.09l6-24c1.89-7.57-3.84-14.91-11.65-14.91zm-352.06-17.83c7.29-18.22 24.94-30.17 44.57-30.17h127c19.63 0 37.28 11.95 44.57 30.17L384 208H128l19.93-49.83zM96 319.8c-19.2 0-32-12.76-32-31.9S76.8 256 96 256s48 28.71 48 47.85-28.8 15.95-48 15.95zm320 0c-19.2 0-48 3.19-48-15.95S396.8 256 416 256s32 12.76 32 31.9-12.8 31.9-32 31.9z",
        fillColor: '#006c00',
        strokeColor: '#ecff00',
        fillOpacity: 1,

        strokeWeight: 1,
        scale: .10,
        anchor: anchor

    };

    var positionMarker = new google.maps.Marker({
        position: new google.maps.LatLng(
            (jsonVar[(Object.keys(jsonVar).length - 1)][(Object.keys(jsonVar[(Object.keys(jsonVar).length - 1)]).length - 1)].lat),
            jsonVar[(Object.keys(jsonVar).length - 1)][(Object.keys(jsonVar[(Object.keys(jsonVar).length - 1)]).length - 1)].lng),
        icon: car_icon,

        map: map


    });

}


//SLIDER UNDER MAP


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



