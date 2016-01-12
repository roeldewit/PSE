$(function () {

    if (Cookies.get('currency') !== "EUR" &&
            Cookies.get('currency') !== "GBP") {
        Cookies.set("currency", "EUR");
    }

    /**
     * Performs currency conversion and formatting on page load.
     */
    if (Cookies.get('currency') !== "EUR") {
        var cookie = Cookies.get('currency');

        $.get('/app/services/currency/rates', function (data) {
            var rate = data[cookie];

            $('span.currency-convertable').each(function () {
                var number = parseFloat($(this).html()) * rate;
                var conversion = $.format.number(number, "#,##0.00");
                $(this).html(conversion);
            });

            $('span.currency-sign-convertable').each(function () {
                switch (Cookies.get('currency')) {
                    case "EUR":
                        $(this).html('&euro;');
                        break;
                    case "GBP":
                        $(this).html('&pound;');
                        break;
                    default:
                        $(this).html('&euro;');
                        break;
                }
            });
        });
    } else {
        $('span.currency-convertable').each(function () {
            var number = parseFloat($(this).html());
            var conversion = $.format.number(number, "#,##0.00");
            $(this).html(conversion);
        });
    }

    /**
     * Handles requests to change the currency.
     */
    $('.currency-select').change(function () {
        Cookies.set('currency', $(this).val());
        location.reload();
    });

    /**
     * Handles user request to add an item to the cart.
     */
    $('.add-to-cart').click(function () {
        var picture_id = $('form#add_to_cart').
                find('input[name="picture_id"]').val();
        var product_type_id = $('form#add_to_cart').
                find('.product-display.selected').
                find('input[name="product_id"]').val();
        var amount = $('form#add_to_cart input#amount').val();
        var color = $('form#add_to_cart').
                find('.color-display.selected').
                find('input[name="color"]').val();
        var x1 = $('form#add_to_cart').
                find('input[name="x1"]').val();
        var x2 = $('form#add_to_cart').
                find('input[name="x2"]').val();
        var y1 = $('form#add_to_cart').
                find('input[name="y1"]').val();
        var y2 = $('form#add_to_cart').
                find('input[name="y2"]').val();

        $.post("/app/customers/cart/ajax/add",
                JSON.stringify({
                    "picture_id": picture_id,
                    "product_type_id": product_type_id,
                    "amount": amount,
                    "color": color,
                    "x1": x1,
                    "x2": x2,
                    "y1": y1,
                    "y2": y2
                }),
                function (data) {
                    window.location.replace("/app/customers/cart");
                });
    });

    /**
     * Allows users to select specific options.
     */
    $('.product-display').click(function () {
        $(this).siblings('.product-display').removeClass('selected');
        $(this).addClass('selected');
    });

    /**
     * Allows users to select specific options.
     */
    $('.color-display').click(function () {
        $(this).siblings('.color-display').removeClass('selected');
        $(this).addClass('selected');
    });

    /**
     * Handles user request to change the amount of an item in the cart.
     */
    $('.cart-product-amount').change(function () {
        var amount = $(this).val();
        var entry_id = $(this).siblings('input[name="entry_id"]').val();

        $.post("/app/customers/cart/ajax/amount",
                JSON.stringify({
                    "entry_id": entry_id,
                    "amount": amount
                }),
                function (data) {
                    window.location.replace("/app/customers/cart");
                });
    });

    /**
     * Handles user request to remove an item from the cart.
     */
    $('.cart-remove-item').click(function () {
        var entry_id = $(this).siblings('input[name="entry_id"]').val();

        $.post("/app/customers/cart/ajax/remove",
                JSON.stringify({
                    "entry_id": entry_id
                }),
                function (data) {
                    window.location.replace("/app/customers/cart");
                });
    });

    /**
     * Handles user request to commit an order.
     */
    $('.commit-order').click(function () {
        $.post("/app/customers/cart/ajax/commit")
                .done(function (data) {
                    var order_id = JSON.parse(data)["order_id"];
                    window.location.replace("/app/payment/pay/" + order_id);
                })
                .fail(function (data) {
                    if (data.status === 403) {
                        window.location.replace("/app/login?redirect=" + window.location.pathname);
                    } else {
                        window.location.reload();
                    }
                });
    });
});