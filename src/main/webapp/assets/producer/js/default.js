$(function() {
    
    $('a.approve-handler').click(function() {
        var element = $(this);
        $.post( "/app/producer/dashboard/submissions/ajax", 
            JSON.stringify({"option": "approve", "picture_id": element.attr('picture_id') }),
            function( data ) {
            element.parents('tr').first().find('td')
                    .wrapInner('<div style="display: block;" />')
                    .parent()
                    .find('td > div')
                    .slideUp(700, function(){
            
                $(this).parent().parent().remove();    
                if ($('table.submitted-photos-table tr td').size() < 1) {
                    $('table.submitted-photos-table').hide();
                }
            
            });
        });
    });
    
    $('a.reject-handler').click(function() {
        var element = $(this);
        $.post( "/app/producer/dashboard/submissions/ajax", 
            JSON.stringify({"option": "reject", "picture_id": element.attr('picture_id') }),
            function( data ) {
            element.parents('tr').first().find('td')
                    .wrapInner('<div style="display: block;" />')
                    .parent()
                    .find('td > div')
                    .slideUp(700, function(){
            
                $(this).parent().parent().remove();    
                if ($('table.submitted-photos-table tr td').size() < 1) {
                    $('table.submitted-photos-table').hide();
                }
            
            });
        });
    });
});