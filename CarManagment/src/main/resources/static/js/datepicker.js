$(function () {


    // selected date is stored in cookie for keeping selected date after refresh
    // here it is retrieved
    if (document.cookie) {
        var dateCookie = document.cookie
            .split('; ')
            .find(row => row.startsWith('date'))
            .split('=')[1];

    }


    $("#datepicker").datepicker({
        monthNames: ["Styczeń", "Luty", "Marzec", "Kwiecień", "Maj", "Czerwiec", "Lipiec", "Sierpień", "Wrzesień", "Pażdziernik", "Listopad", "Grudzień"],
        firstDay: 1,
        defaultDate: new Date(dateCookie),
        select: new Date(dateCookie),
        dayNamesMin: ["Ni", "Po", "Wt", "Śr", "Cz", "Pt", "So"],

        onSelect: function (selected) {
            var regNumber = regNumberTl;
            var date = new Date(selected);

            // make cookie valid for 1 sec for caching last picked date
            var now = new Date();
            now.setTime(now.getTime() + 1000);
            // store date cookie for keeping selected date after refresh
            document.cookie = "date=" + date + "; expires=" + now.toUTCString() + "; path=/;";

            var url = "/gps/showmap?regNumber=" + regNumber + "&timestamp=" + date.getTime();

            window.location.assign(url);
        },


    });

});

