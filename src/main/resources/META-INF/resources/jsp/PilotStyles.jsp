<%@ page contentType="text/css;charset=UTF-8"%>
<%@ page trimDirectiveWhitespaces="true"%>
<%@ page import="com.tokenplay.ue4.model.*"%>
<%@ page import="java.util.*"%>
<%
	Map theme = (Map)request.getAttribute("theme");
	if (theme != null)
	{
%>
/* CSS Theme <%= request.getAttribute("theme-name")%> */
<%
		Map<String, String> attributeValues = new HashMap<String,String>();

		// Right now, fonts hardcoded
		attributeValues.put("tech-font-stack","'Orbitron', Arial, sans-serif");
		attributeValues.put("secondary-font-stack","Optima, Segoe, \"Segoe UI\", Candara, Calibri, Arial, sans-serif");
		
		// Right now, background hardcoded
		attributeValues.put("main-background","rgba(0, 0, 0, 0.85)");
		attributeValues.putAll(theme);
%>
<%--
\$((?:\w|\-)*)
<%= attributeValues.get\("\1"\) %>
--%>
body, a, a:hover, a:focus {
  color: <%= attributeValues.get("main-color") %>;
}

table {
  border: thin solid <%= attributeValues.get("light-color") %>;
  color: <%= attributeValues.get("font-data-color") %>;	
}

table > thead > tr > th, table > tbody > tr > th, table > tfoot > tr > th, table > thead > tr > td, table > tbody > tr > td, table > tfoot > tr > td {
  border-top: thin solid <%= attributeValues.get("light-color") %>;
}

.ui-widget-content, .ui-state-default, .ui-widget-content .ui-state-default, .ui-widget-header .ui-state-default {
	color: <%= attributeValues.get("main-color") %>;
    border-color: <%= attributeValues.get("light-color") %>;
}

.color-menu {
  background: <%= attributeValues.get("main-background") %>;
}

.color-item A:hover {
  background: <%= attributeValues.get("main-background") %> !important;
}

.slider-range .ui-slider-range, .quality-slider-range .ui-slider-range, .texture-quality-slider-range .ui-slider-range, .resolution-slider-range .ui-slider-range, #slider-mouse_sense .ui-slider-range, #slider-yaw_sense .ui-slider-range, #slider-pitch_sense .ui-slider-range, #login-volume-slider .ui-slider-range, .volume-slider .ui-slider-range{
  background: <%= attributeValues.get("main-color") %>;
  background: linear-gradient(45deg, <%= attributeValues.get("main-color") %> 25%, <%= attributeValues.get("lightbrilliant-color") %>);
}

div.neon i {
  font-size: 1.5em;
  text-shadow: 0 0 2px <%= attributeValues.get("light-color") %>, 
	0 0 1px <%= attributeValues.get("main-color") %>, 
	0 0 3px <%= attributeValues.get("main-color") %>, 
	0 0 5px <%= attributeValues.get("main-color") %>, 
	5px 9px 5px rgba(0, 0, 0, 0.5);
}

div.neon i.off {
	color: rgba(46, 46, 46, 0.61);
	text-shadow: 7px 7px 5px <%= attributeValues.get("main-background") %>;
}

.ui-widget-header {
  color: <%= attributeValues.get("font-data-color") %>;
}

table.dataTable tr, #gear-config-window .input-group-addon {
  color: <%= attributeValues.get("font-data-color") %>;
}

#chat-tabs li a, #option-tabs li a {
 border: thin solid <%= attributeValues.get("light-color") %>;
 border-bottom: none!important;
}

@keyframes blink {50%{border: thin solid <%= attributeValues.get("personal-color") %>;}}

.animate .bar {
    background-color: <%= attributeValues.get("main-color") %>;
    background-image: linear-gradient(45deg, <%= attributeValues.get("main-color") %> 25%, <%= attributeValues.get("lightbrilliant-color") %>);
}

#login_msg_panel, #server_msg_panel {
    font-family: <%= attributeValues.get("secondary-font-stack") %>;
}

#modalMsg, .game_version {
 font-family: <%= attributeValues.get("tech-font-stack") %>;
 color: <%= attributeValues.get("main-color") %>;
 }

div.neon i {
 font-family: <%= attributeValues.get("tech-font-stack") %>;
 color: <%= attributeValues.get("main-color") %>;
 }

.modal-title, .panel-default .panel-body table tr th, .panel-default .panel-body table tr td {
 font-family: <%= attributeValues.get("tech-font-stack") %>;
 color: <%= attributeValues.get("main-color") %>;
 }

 #chat-tabs, #chat-tabs li, #option-tabs li {
 color: <%= attributeValues.get("main-color") %>;
 background-color: <%= attributeValues.get("main-background") %>;
}

#chat-tabs li a, #option-tabs li a {
 color: <%= attributeValues.get("main-color") %>;
 color: <%= attributeValues.get("font-data-color") %>;
 background-color: <%= attributeValues.get("main-background") %>;
 background: <%= attributeValues.get("main-color") %>;
 font-family: <%= attributeValues.get("secondary-font-stack") %>;
}

#chat-tabs li.active a, #option-tabs li.active a, .server_status{
 border: thin solid <%= attributeValues.get("main-color") %>;
 color: <%= attributeValues.get("font-data-color") %>;
 color: <%= attributeValues.get("main-color") %>;
 background: <%= attributeValues.get("main-background") %>;
 border-bottom: none!important;
}

div#server_table_length select, div#matches_table_length select, .my_nick, #profile_table tr th {
 color: <%= attributeValues.get("main-color") %>;
}

#chat-table, input.chat_message_send:disabled, #gear-config-window, .server_status, #options-tab, #profile-tab, #server-tab {
 background-color: <%= attributeValues.get("main-background") %>;
}

.color-menu-instructions {
  font-family: <%= attributeValues.get("secondary-font-stack") %>;
}

.slider-value {
  color: <%= attributeValues.get("main-color") %>;
  border: thin solid <%= attributeValues.get("main-color") %>;
  background-color: <%= attributeValues.get("main-background") %>;
}

#login-music-volume button{
  color: <%= attributeValues.get("main-color") %> !important;
}

.panel-default .panel-heading {
  background-color: <%= attributeValues.get("main-background") %>;
  color: <%= attributeValues.get("main-color") %> !important;
  border-color: <%= attributeValues.get("main-color") %> !important;
}

.panel {
  border-color: <%= attributeValues.get("main-color") %> !important;
}

.panel-default .panel-body, .panel-default .panel-body table {
  background-color: <%= attributeValues.get("main-background") %>;
  color: <%= attributeValues.get("main-color") %> !important;
}

.fg-toolbar {
 border: 1px solid <%= attributeValues.get("light-color") %>;
}

table.dataTable thead th, table.dataTable tbody td {
 border: 1px solid <%= attributeValues.get("main-color") %>!important;
}

.modal-content {
  border: 2px solid <%= attributeValues.get("light-color") %>;
  background-color: <%= attributeValues.get("main-background") %>;
}

.form-signin {
  border: 1px solid <%= attributeValues.get("light-color") %>;
  background-color: <%= attributeValues.get("main-background") %>;
}

.form-hosting, #hosted-server-info {
  border: 1px solid <%= attributeValues.get("light-color") %>;
  background-color: <%= attributeValues.get("main-background") %>;
}

.area-chat{
 border-right: 1px solid <%= attributeValues.get("light-color") %>;
}

#gear-config-window  .custom-input-group{
	//background: <%= attributeValues.get("light-color") %>;
}

#gear-config-window  fieldset  legend{
    //border-color: <%= attributeValues.get("main-color") %>;
	//color: <%= attributeValues.get("main-color") %>;
}

a.button, a.button:active, a.button:focus, input.button, input.button:active, input.button:focus, a.Button, a.Button:active, a.Button:focus, input.Button, input.Button:active, input.Button:focus, .btn-primary, .btn-danger, .btn-default, li.button {
 background-color: <%= attributeValues.get("main-background") %>!important;
 border-color: <%= attributeValues.get("main-color") %> -moz-use-text-color -moz-use-text-color;
 color: <%= attributeValues.get("main-color") %>;
 font-family: <%= attributeValues.get("secondary-font-stack") %>;
 box-shadow: 0 10px 7px -10px <%= attributeValues.get("main-color") %> inset, 0 -10px 10px -5px <%= attributeValues.get("light-color") %> inset, 0 10px 10px -5px #000;
}

a.button:hover, input.button:hover {
 box-shadow: 0 10px 7px -10px <%= attributeValues.get("main-color") %> inset, 0 -10px 10px -5px <%= attributeValues.get("lightbrilliant-color") %> inset, 0 10px 10px -5px #000;
}

.form-control, .InputBox, .TextBox, select, .token-input-list, .input-group-addon {
    border-color: <%= attributeValues.get("light-color") %>;
}

.input-group-addon {
    color: <%= attributeValues.get("main-color") %>;
}

.announce .message {
 font-family: <%= attributeValues.get("secondary-font-stack") %>;
 color: Green; /* <%= attributeValues.get("main-color") %>; */
}

.pagination > .active > a, .pagination > .active > span, .pagination > .active > a:hover, .pagination > .active > span:hover, .pagination > .active > a:focus, .pagination > .active > span:focus {
    background-color: <%= attributeValues.get("light-color") %>!important;
    border-color: <%= attributeValues.get("light-color") %>!important;
    color: <%= attributeValues.get("main-background") %>;
}

.pagination > li > a, .pagination > li > span, .pagination > li > a:focus, .pagination > li > span:focus {
    background-color: transparent!important;
    border-color: <%= attributeValues.get("light-color") %>;
    color: <%= attributeValues.get("main-color") %>;
}

.pagination > li > a:hover, .pagination > li > span:hover {
    background-color: transparent!important;
    border-color: <%= attributeValues.get("light-color") %>;
    color: <%= attributeValues.get("main-color") %>;
}

.pagination > li.disabled > a, .pagination > li.disabled > span, .pagination > li.disabled > a:hover, .pagination > li.disabled > span:hover, .pagination > li.disabled > a:focus, .pagination > li.disabled > span:focus
{
    background-color: transparent!important;
    border-color: gray;
    color: gray;
}


.button-link:hover {
    background: <%= attributeValues.get("main-background") %>;
    color: <%= attributeValues.get("lightbrilliant-color") %>;
    border-color:<%= attributeValues.get("light-color") %>;
}

.button-link {
    background: <%= attributeValues.get("lightbrilliant-color") %>;
    border-color: <%= attributeValues.get("light-color") %>;
    color: <%= attributeValues.get("main-background") %>;
}

.my_nick {
	color: <%= attributeValues.get("personal-color") %>;
}

.system {
 color: <%= attributeValues.get("system-color") %>;
}

.ui-widget-content {
    border-color: <%= attributeValues.get("light-color") %>;
}

.ui-widget-content .ui-state-focus {
    background: <%= attributeValues.get("lightbrilliant-color") %>;
    color: <%= attributeValues.get("main-background") %>;
}

.development_server TD{
 color: <%= attributeValues.get("system-color") %>;
}

.color-box {
 	border: 1px solid <%= attributeValues.get("light-color") %>;
}

input[type=checkbox]:checked + label:before {
    color: <%= attributeValues.get("main-color") %>;
    border: 1px solid <%= attributeValues.get("main-color") %>;
}

.nav-tabs {
    border-bottom: thin solid <%= attributeValues.get("light-color") %>;
}

UL.dropdown-menu LI, UL.dropdown-menu LI A {
	color: <%= attributeValues.get("main-color") %>;
}
.open .dropdown-toggle.btn-default {
	color: <%= attributeValues.get("lightbrilliant-color") %>;
}

.dropdown-menu {
	border-color: <%= attributeValues.get("light-color") %>;
}

.dropdown-menu > li > a:hover {
	color: <%= attributeValues.get("font-data-color") %>;
	border: 1px solid <%= attributeValues.get("main-color") %>;
}

#save_theme, #save_instance_theme {
	color: <%= attributeValues.get("personal-color") %>!important;
}

#server_table TR:hover TD BUTTON:hover {
  border: thin solid <%= attributeValues.get("lightbrilliant-color") %>!important;
}

<%
	}
%>