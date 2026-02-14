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

}
