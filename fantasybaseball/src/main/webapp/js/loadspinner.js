var loadspinner2;
loadspinner2 = loadspinner2 || (function () {
	console.log("In loadspinner");
    var loaderDiv = $('#profile-text-spinner');
    console.log("In loadspinner2");
    return {
        showLoader: function(element) {
        	console.log("In loadspinner3");
        	$(element).hide();
        	// $(element).before(loaderDiv);
        	loaderDiv.show();
        	console.log("In loadspinner4");
        },
        hideLoader: function (element) {
        	loaderDiv.hide();
        	$(element).show();
        },

    };
})();
