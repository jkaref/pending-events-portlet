<%-- 
     Copyright (C) 2017 [j]karef GmbH
     
     Permission is hereby granted, free of charge, to any person obtaining a copy
     of this software and associated documentation files (the "Software"), to deal
     in the Software without restriction, including without limitation the rights
     to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
     copies of the Software, and to permit persons to whom the Software is
     furnished to do so, subject to the following conditions:
     
     The above copyright notice and this permission notice shall be included in
     all copies or substantial portions of the Software.
     
     THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
     IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
     FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
     AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
     LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
     OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
     SOFTWARE.
--%>
<%@page import="javax.portlet.PortletMode"%>
<%@ include file="../init.jsp" %>

<liferay-portlet:actionURL var="actionUrl" name="savePreferences" />
<liferay-portlet:renderURL var="backURL" portletMode="<%= PortletMode.VIEW.toString() %>"/>

<aui:form action="${actionUrl}" method="POST" name="edit-form" cssClass="calendar-form">

    <aui:input type="text" name="limit" label="label.input.max-entries" value="${limit}">
        <aui:validator name="max">50</aui:validator>
        <aui:validator name="type"></aui:validator>
    </aui:input>
    
    <aui:input name="background_color" value="${background_color}" 
    		cssClass="background-color-picker" 
    		label="label.input.background-color" 
    	/>
   
    <aui:select name="calendar_id" label="label.select.calendar">
    		<aui:option value="">------</aui:option>
    
        <c:forEach items="${calendars}" var="calendar">
            <c:set var="isSelected" value="${calendar.calendarId eq calendar_id ? true :  false}"/>
			
            <aui:option value="${calendar.calendarId}" selected="${isSelected}">
                ${calendar.getName(pageContext.request.locale)}
            </aui:option>
        </c:forEach>
    </aui:select>

    <c:choose>
		<c:when test="${not empty calendar_id}">		
			<c:choose>
				<c:when test="${not empty visible_events}">		
					<%@ include file="/html/upcoming-events/includes/event_list.jspf" %>				
				</c:when>
			
				<c:otherwise>
					<div class="alert alert-info">
						<liferay-ui:message key="message.warning.no-events-to-display" />
					</div>
				</c:otherwise>
			</c:choose>				
		</c:when>
	
		<c:otherwise>
			<div class="alert alert-info">
				<liferay-ui:message key="message.warning.no-calendar-selected" />
			</div>
		</c:otherwise>
	</c:choose>

    <aui:input type="hidden" name="hidden_events_json" value="${hidden_events_json}" />
    
    <aui:button-row>
        <aui:button type="button" value="back" href="${backURL}" />
        <aui:button type="submit" value="save" />
    </aui:button-row>
        
</aui:form>

<aui:script use="upcoming-events-edit">
	new A.UpcomingEventsEdit({
		namespace: '<portlet:namespace />',
		hiddenEventsJson: '${hidden_events_json}'
	});
</aui:script>
