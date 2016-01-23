var loadspinner;
loadspinner = loadspinner || (function () {
    var loaderDiv = $('<div class="text-center"><i class="fa fa-refresh fa-spin fa-5x" id="projections-spinner"></i><p>Retrieving Data...</p></div>');

    return {
        showLoader: function(element) {
        	$(element).hide();
        	$(element).before(loaderDiv);
        	loaderDiv.show();
        },
        hideLoader: function (element) {
        	loaderDiv.hide();
        	$(element).show();
        },

    };
})();
