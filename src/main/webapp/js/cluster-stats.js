function toggleNodeView(id) {
	var el = document.getElementById(id)
	
	if (el.style.visibility == "hidden")
		toggleNodeShow(id)
	else
		toggleNodeHide(id)
}

function toggleNodeShow(id) {
	document.getElementById(id).style.visibility = "visible"
	document.getElementById(id).style.display = "block"
}

function toggleNodeHide(id) {
	document.getElementById(id).style.visibility = "hidden"
	document.getElementById(id).style.display = "none"
}