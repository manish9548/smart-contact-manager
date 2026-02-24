console.log("this is javascript code");

function toggleSidebar() {

    console.log("toggleSidebar called");

    let sidebar = document.querySelector(".sidebar");
    let content = document.querySelector(".content");

    if (sidebar.style.display === "none") {

        sidebar.style.display = "block";
        content.style.marginLeft = "20%";

    } else {

        sidebar.style.display = "none";
        content.style.marginLeft = "0%";

    }
	

};

const search = () => {
    let query = $("#search-input").val();

    if (query == '') {
        $(".search-result").hide();
    } else {
        // Correcting the URL with backticks
        let url = `http://localhost:8080/search/${query}`;

        fetch(url)
            .then((response) => response.json())
            .then((data) => {
                let text = `<div class='list-group shadow'>`;

                data.forEach((contact) => {
                    // Correcting the link structure to match your Controller
                    text += `<a href='/user/contact/${contact.cId}' class='list-group-item list-group-item-action'> 
                                <i class="fa-solid fa-user-circle me-2"></i> ${contact.name} 
                             </a>`;
                });

                text += `</div>`;

                $(".search-result").html(text).show();
            })
            .catch((error) => {
                console.error("Error fetching data:", error);
            });
    }
};