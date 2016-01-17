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
    
    /**
     * Handles the user request to change the amount of stock of a product.
     */
    $('input#stock-amount').change(function() {
        var amount = $(this).val();
        var product_type_id = $(this).siblings('input[name="product_type_id"]').val();
        
        $.post( "/app/producer/dashboard/products/ajax/edit-stock", 
            JSON.stringify({
                "product_type_id": product_type_id,
                "amount": amount
            }),
            function( data ) {
                window.location.replace("/app/producer/dashboard/products");
            });
    });
});