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
        dayNamesMin: ["Ni", "Po", "Wt", "Sr", "Cz", "Pt", "So"],

        onSelect: function (selected) {
            var regNumber = regNumberTl;
            var date = new Date(selected);

            // store date cookie for keeping selected date after refresh
            document.cookie = "date=" + date + "; path=/";

            var url = "/gps/showmap?regNumber=" + regNumber + "&timestamp=" + date.getTime();

            window.location.assign(url);
        },


    });

});

