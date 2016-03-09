function selectAndScrollToOption(select, option) {
    $select = $(select);

    // Store the currently selected options
    var $selectedOptions = $select.find("option:selected");

    // Select the new option using its selected property and selectedIndex.
    // This seems to make the select scroll to the desired place in all major
    // browsers
    option.selected = true; // Required for old IE
    select.selectedIndex = option.index;

    // Measure the vertical scrolling
    var scrollTop = $select.scrollTop();

    // Re-select the original options
    $selectedOptions.prop("selected", true);

    // Scroll to the correct place
    $select.scrollTop(scrollTop);
}