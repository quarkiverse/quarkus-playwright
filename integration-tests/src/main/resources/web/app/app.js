import "bootstrap/dist/css/bootstrap.css";
import $ from 'jquery';

function endCountdown() {
    $.get('/hello', (data) => {
        $('.toast-title').html(`RECEIVED DATA FROM QUARKUS API`);
        $('.toast-body').addClass("received").html(`<b>${data}</b>`);
    });
}

let count = 5;

const timer = setInterval(function () {
    handleTimer(count);
}, 50);

function handleTimer() {
    if (count === 0) {
        clearInterval(timer);
        endCountdown();
    } else {
        $('.toast-title').html(`CALLING QUARKUS API IN`);
        $('.toast-body').html(`<div class="count">${count}</div>`);
        count--;
    }
}

handleTimer(count);

