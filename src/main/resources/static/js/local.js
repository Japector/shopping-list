function formToJson(form) {
    var arrayData = form.serializeArray();
    var jsonData = {};
    $.each(arrayData, function() {
        jsonData[this.name] = this.value || '';
    });
    return JSON.stringify(jsonData);
}