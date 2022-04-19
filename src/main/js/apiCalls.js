function fetchFromAPI(api, args) {
    var url = "/api/"+api;

    if (typeof args != undefined)
        url += '?' + (new URLSearchParams(args)).toString();

    return fetch(url).then(response=>response.json());
}

function fetchBuyItem(id) {
    return fetchFromAPI('buyItem', {id});
}

export {fetchFromAPI, fetchBuyItem}