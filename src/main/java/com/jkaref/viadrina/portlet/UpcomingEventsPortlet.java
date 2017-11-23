/**
 *  Copyright (C) 2017 [j]karef GmbH
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in
 *  all copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */



package com.jkaref.viadrina.portlet;

import java.io.IOException;
import java.util.List;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.PortletPreferences;
import javax.portlet.ReadOnlyException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.ValidatorException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jkaref.viadrina.portlet.param.Param;
import com.jkaref.viadrina.portlet.service.ServiceUtil;
import com.jkaref.viadrina.portlet.util.ParamUtil;
import com.liferay.calendar.model.Calendar;
import com.liferay.calendar.model.CalendarBooking;
import com.liferay.util.bridges.mvc.MVCPortlet;

/**
 * This portlet displays upcoming events from a liferay calendar. The calendar
 * from which the events are taken must be configured before use via portlet
 * settings.
 *
 * Additional features include:
 * - customizable number of events to display
 * - customizable background color
 * - manual exclusion of events
 *
 */
public class UpcomingEventsPortlet extends MVCPortlet {

    private static final Logger LOG = LoggerFactory.getLogger(UpcomingEventsPortlet.class);


    /*
     * Main render method. Uses limit, calendar id, and hidden events ids and background color
     * from portlet preferences to retrieve
     * (non-Javadoc)
     * @see com.liferay.util.bridges.mvc.MVCPortlet#doView(javax.portlet.RenderRequest, javax.portlet.RenderResponse)
     */
    @Override
    public void doView(RenderRequest request, RenderResponse response)
            throws IOException, PortletException {

            PortletPreferences preferences = request.getPreferences();
            String limit = ParamUtil.getLimit(preferences);
            String calendarId = ParamUtil.getCalendarId(preferences);
            String hiddenEventsIds = ParamUtil.getHiddenEventsJson(preferences);
            String backgroundColor = ParamUtil.getBackgroundColor(preferences);

            List<CalendarBooking> visibleEvents =
                    ServiceUtil.getVisibleEvents(limit, calendarId, hiddenEventsIds);

            request.setAttribute(Param.VISIBLE_EVENTS.string(), visibleEvents);
            request.setAttribute(Param.CALENDAR_ID.string(), calendarId);
            request.setAttribute(Param.BACKGROUND_COLOR.string(), backgroundColor);

            LOG.info("[doView] - Showing {} {}.",
                    visibleEvents.size(),
                    visibleEvents.size() == 1 ? "event" : "events"
            );

            super.doView(request, response);
    }

    @Override
    public void doEdit(RenderRequest request, RenderResponse response)
            throws IOException, PortletException {

        PortletPreferences preferences = request.getPreferences();
        String limit = ParamUtil.getLimit(preferences);
        String calendarId = ParamUtil.getCalendarId(preferences);
        String hiddenEventJson = ParamUtil.getHiddenEventsJson(preferences);
        String backgroundColor = ParamUtil.getBackgroundColor(preferences);

        List<Calendar> calendars = ServiceUtil.getCalendars();

        List<CalendarBooking> visibleEvents =
                ServiceUtil.getVisibleEvents(limit, calendarId);

        List<CalendarBooking> hiddenEvents =
                ServiceUtil.getHiddenEvents(hiddenEventJson);

        LOG.info("[doEdit] - Showing {} {} ({} hidden).",
                visibleEvents.size(),
                visibleEvents.size() == 1 ? "event" : "events",
                hiddenEvents.size()
        );

        request.setAttribute(Param.LIMIT.string(), limit);
        request.setAttribute(Param.CALENDARS.string(), calendars);
        request.setAttribute(Param.CALENDAR_ID.string(), calendarId);
        request.setAttribute(Param.VISIBLE_EVENTS.string(), visibleEvents);
        request.setAttribute(Param.HIDDEN_EVENTS.string(), hiddenEvents);
        request.setAttribute(Param.HIDDEN_EVENTS_JSON.string(), hiddenEventJson);
        request.setAttribute(Param.BACKGROUND_COLOR.string(), backgroundColor);

        super.doEdit(request, response);
    }


    public void savePreferences(ActionRequest request, ActionResponse response)
            throws ValidatorException, IOException {

        String limit = ParamUtil.getLimit(request);
        String calendarId = ParamUtil.getCalendarId(request);
        String hiddenEventsJson = ParamUtil.getHiddenEventsJson(request);
        String backgroundColor = ParamUtil.getBackgroundColor(request);

        LOG.debug("[savePreferences] - Saving preferences ["
                + "limit:{}, calendarId:{}, hiddenEventsJson:{}, backgroundColor:{}] )",
                limit, calendarId, hiddenEventsJson, backgroundColor);

        PortletPreferences preferences = request.getPreferences();

        try {

            preferences.setValue(Param.LIMIT.string(), limit);
            preferences.setValue(Param.CALENDAR_ID.string(), calendarId);
            preferences.setValue(Param.HIDDEN_EVENTS_JSON.string(), hiddenEventsJson);
            preferences.setValue(Param.BACKGROUND_COLOR.string(), backgroundColor);

            preferences.store();

        } catch (ReadOnlyException e) {
            LOG.warn("[updatePreferences] - Failed to save preferences.", e);
        }
    }

}












