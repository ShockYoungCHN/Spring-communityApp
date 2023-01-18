$(function(){
	$("#publishBtn").click(publish);
});

function publish() {
	$("#publishModal").modal("hide");

    // Set the CSRF token into the message header of the request before sending the AJAX request
    // var token = $("meta[name='_csrf']").attr("content");
    // var header = $("meta[name='_csrf_header']").attr("content");
    // $(document).ajaxSend(function (e, xhr, options) {
    //    xhr.setRequestHeader(header, token);
    // });

    // Get the title and content
    var title = $("#recipient-name").val();
    var content = $("#message-text").val();

    // Sending an asynchronous request (POST)
    $.post(
        CONTEXT_PATH + "/discuss/add",
        {"title": title, "content": content},
        function (data) {
            data = $.parseJSON(data);
            // Display the return message in the prompt box
            $("#hintBody").text(data.msg);
            // Show alert box
            $("#hintModal").modal("show");
            // the alert box will be automatically hidden after 2 seconds
            setTimeout(function () {
                $("#hintModal").modal("hide");
                // refresh the webpage, data.code==0 means the posting is successful
                if (data.code == 0) {
                    window.location.reload();
                }
            }, 2000);
        }
    );
}