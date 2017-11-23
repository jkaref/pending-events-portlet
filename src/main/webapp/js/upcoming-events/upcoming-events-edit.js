/** 
  * Copyright (C) 2017 [j]karef GmbH
  * 
  * Permission is hereby granted, free of charge, to any person obtaining a copy
  * of this software and associated documentation files (the "Software"), to deal
  * in the Software without restriction, including without limitation the rights
  * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
  * copies of the Software, and to permit persons to whom the Software is
  * furnished to do so, subject to the following conditions:
  * 
  * The above copyright notice and this permission notice shall be included in
  * all copies or substantial portions of the Software.
  * 
  * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
  * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
  * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
  * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
  * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
  * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
  * SOFTWARE.
 */
AUI.add('upcoming-events-edit', function (A) {
	'use strict';
	
	var EVENT_CONTAINER = '#event-list',
		EDIT_FORM = '#edit-form',
		HIDDEN_INPUT = '#hidden_events_json',
		COLOR_PICKER = '.background-color-picker';
	
	A.UpcomingEventsEdit = A.Base.create('upcoming-events-edit', A.Base, [Liferay.PortletBase], {
		
		
		// STATE
		dataSet: {},
		
		// LIFECYCLE
		initializer: function () {
			
			var self = this,
				container = A.one(EVENT_CONTAINER),
				form = this.one(EDIT_FORM),
				input = this.one(HIDDEN_INPUT),
				json = this.get('hiddenEventsJson'),
				colorPicker;
						
			this.dataSet = new A.DataSet({
				getKey: function (value) {
					return value;
				}
			});
						
			this.dataSet.addAll(JSON.parse(json));
			
			if (container) {
				container.delegate('click', function (event) {
					
					self._updateData(this);
					self._toggleClasses(this);
					
				}, 'li');
			}
			
			colorPicker = new A.ColorPickerPopover({
				trigger: COLOR_PICKER,
				zIndex: 2,
				strings: {
		            cancel: Liferay.Language.get('label.colorpicker.cancel'),
		            header: Liferay.Language.get('label.colorpicker.choose-custom-color'),
		            more: Liferay.Language.get('label.colorpicker.more-colors'),
		            noColor: Liferay.Language.get('label.colorpicker.no-color'),
		            none: Liferay.Language.get('label.colorpicker.none'),
		            ok: 'OK'
			    },
			}).render();
			
			colorPicker.on('select', function (event) {
				event.trigger.set('value', event.color);
				
				A.all('.event-date time').each(function () {
					this.setStyle('backgroundColor', event.color);
				});
			});
			
			
			form.on('submit', function (event) {
				event.halt();
				
				var results = [];
				
				A.Object.each(self.dataSet.get('items'), function (id) {
					results.push(id);					
				});
				
				input.val(JSON.stringify(results));
			
				form.submit();
			});
			
		},

	
		// HELPERS
		_updateData: function (item) {
			
			var id = item.attr('data-id');
			
			if(item.hasClass('event-hidden')) {
				this.dataSet.remove(id);
			
			} else {
				this.dataSet.add(id);
				
			}

		},
				
		_toggleClasses: function (item) {
			
			var button = item.one('.event-button');
			
			if (item.hasClass('event-hidden')) {
				item.replaceClass('event-hidden', 'event-visible');
				button.replaceClass('icon-eye-close', 'icon-eye-open');
			} else {
				item.replaceClass('event-visible', 'event-hidden');	
				button.replaceClass('icon-eye-open', 'icon-eye-close');
			}		
				
		}
	
	}, {
        ATTRS: {
            hiddenEventsJson: {
            		value: ''
            }
        }
	});
	
	
}, '2.5.0', {
	requires: [
		'aui-base',
		'aui-data-set-deprecated',
		'aui-color-picker-popover',
		'liferay-portlet-base'
	]
});
