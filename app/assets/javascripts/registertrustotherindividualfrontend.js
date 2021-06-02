$(document).ready(function() {

    // =====================================================
    // Back link mimics browser back functionality
    // =====================================================
    // store referrer value to cater for IE - https://developer.microsoft.com/en-us/microsoft-edge/platform/issues/10474810/  */
//    var docReferrer = document.referrer
//    // prevent resubmit warning
//    if (window.history && window.history.replaceState && typeof window.history.replaceState === 'function') {
//        window.history.replaceState(null, null, window.location.href);
//    }
//    $('#back-link').on('click', function(e){
//        e.preventDefault();
//        window.history.back();
//    })

    function beforePrintCall(){
        if($('.no-details').length > 0){
            // store current focussed element to return focus to later
            var fe = document.activeElement;
            // store scroll position
            var scrollPos = window.pageYOffset;
            $('details').not('.open').each(function(){
                $(this).addClass('print--open');
                $(this).find('summary').trigger('click');
            });
            // blur focus off current element in case original cannot take focus back
            $(document.activeElement).blur();
            // return focus if possible
            $(fe).focus();
            // return to scroll pos
            window.scrollTo(0,scrollPos);
        } else {
            $('details').attr("open","open").addClass('print--open');
        }
        $('details.print--open').find('summary').addClass('heading-medium');
    }

    function afterPrintCall(){
        $('details.print--open').find('summary').removeClass('heading-medium');
        if($('.no-details').length > 0){
            // store current focussed element to return focus to later
            var fe = document.activeElement;
            // store scroll position
            var scrollPos = window.pageYOffset;
            $('details.print--open').each(function(){
                $(this).removeClass('print--open');
                $(this).find('summary').trigger('click');
            });
            // blur focus off current element in case original cannot take focus back
            $(document.activeElement).blur();
            // return focus if possible
            $(fe).focus();
            // return to scroll pos
            window.scrollTo(0,scrollPos);
        } else {
            $('details.print--open').removeAttr("open").removeClass('print--open');
        }
    }

    //Chrome
    if(typeof window.matchMedia != 'undefined'){
        mediaQueryList = window.matchMedia('print');
        mediaQueryList.addListener(function(mql) {
            if (mql.matches) {
                beforePrintCall();
            };
            if (!mql.matches) {
                afterPrintCall();
            };
        });
    }

    //Firefox and IE (above does not work)
    window.onbeforeprint = function(){
        beforePrintCall();
    }
    window.onafterprint = function(){
        afterPrintCall();
    }

});