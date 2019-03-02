// League management table in League Details tab
function loadLeagueTeamMgmtTable(data, isInitialLoad)
{
	var data_table;
	var table_element = $('#league_team_mgmt_table');
	var config = {
        "bSort" : true,
        "searching": false,
        "paging": false,
        "info": false,
        "order": [],
		responsive: true,
    	"processing": true,
        data: data,
        select: {
            style:    'single'
        },
        rowId: 'id',
        "createdRow": function ( row, data, index ) {
            if ( data.isuserowner ) {
                $('td', row).css("font-weight", "bold");
            }
        },
        "columns": [
            { "visible": false, "title": "Team ID", "mData": "id", "sDefaultContent": ""},	
            { "visible": false, "title": "isMyTeam", "mData": "isuserowner", "sDefaultContent": ""},	
            { "title": "Team", "mData": "team_name", "sDefaultContent": "", render: $.fn.dataTable.render.ellipsis( 20 )},	
            { "title": "Owner", "mData": "owner_name", "sDefaultContent": "", render: $.fn.dataTable.render.ellipsis( 20 )},
            /*
            { "title": "Bal", "mData": "balance", "render": function ( data, type, row ) 
            	{return "$" + data.toFixed(0);},"sDefaultContent": ""},
            // { "title": "Spots", "mData": "remainingspots", "sDefaultContent": ""},
            { "title": "H", "mData": "hitterspots", "sDefaultContent": ""},
            { "title": "P", "mData": "pitcherspots", "sDefaultContent": ""},
            { "title": "Avg $", "mData": "perplayeramt", "render": function ( data, type, row ) 
            	{return "$" + data.toFixed(2);},"sDefaultContent": ""},
            { "title": "Max $", "mData": "maxbid", "render": function ( data, type, row ) {
        		return "$" + data.toFixed(0);
            }, "sDefaultContent": ""},
			*/
        ]
        };
	
	if (isInitialLoad) 	{
		data_table = table_element.dataTable(config);
	} else {
		data_table = table_element.DataTable();
		data_table.off('select');
		data_table.off('deselect');
		data_table.destroy();
		table_element.empty();
		data_table = table_element.dataTable(config);
		data_table = table_element.DataTable();
	}
	
	var data_table = $('#league_team_mgmt_table').DataTable();
	data_table
    .on( 'select', function ( e, dt, type, indexes ) {
    	
    	var data_table_b = $('#league_team_mgmt_table').DataTable();
    	var row = data_table_b.rows( indexes ).data()[0];

        // Set global variable for player row
        leagueteamselectedrow = row;
    	
    	data_table_b = null;
    	row = null;
    	
    	$("#btn-deleteTeamFromLeague").removeAttr("disabled");
		
    } )
    .on( 'deselect', function ( e, dt, type, indexes ) {
    	$("#btn-deleteTeamFromLeague").attr("disabled","disabled");
    } );

}