/**
 * LocalDatePickerTest.java
 *
 * Copyright (c) 2011-2014, JFXtras
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the organization nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package jfxtras.scene.control.agenda.test;

import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import jfxtras.scene.control.agenda.Agenda;
import jfxtras.test.AssertNode;
import jfxtras.test.JFXtrasGuiTest;
import jfxtras.test.TestUtil;
import junit.framework.Assert;

import org.junit.Test;

/**
 * 
 */
public class AgendaBasicTest extends JFXtrasGuiTest {

	/**
	 * 
	 */
	public Parent getRootNode()
	{
		Locale.setDefault(Locale.ENGLISH);
		
		vbox = new VBox();

		agenda = new Agenda();
		agenda.setDisplayedCalendar(new GregorianCalendar(2014, 0, 1));
		agenda.setPrefSize(1000, 800);
		
        // setup appointment groups
        for (int i = 0; i < 24; i++) {
        	appointmentGroupMap.put("group" + (i < 10 ? "0" : "") + i, new Agenda.AppointmentGroupImpl().withStyleClass("group" + i));
        }
        for (String lId : appointmentGroupMap.keySet())
        {
            Agenda.AppointmentGroup lAppointmentGroup = appointmentGroupMap.get(lId);
            lAppointmentGroup.setDescription(lId);
            agenda.appointmentGroups().add(lAppointmentGroup);
        }

        // accept new appointments
        agenda.createAppointmentCallbackProperty().set(new Callback<Agenda.CalendarRange, Agenda.Appointment>()
        {
            @Override
            public Agenda.Appointment call(Agenda.CalendarRange calendarRange)
            {
                return new Agenda.AppointmentImpl()
                        .withStartTime(calendarRange.getStartCalendar())
                        .withEndTime(calendarRange.getEndCalendar())
                        .withSummary("new")
                        .withDescription("new")
                        .withAppointmentGroup(appointmentGroupMap.get("group01"));
            }
        });
        
		vbox.getChildren().add(agenda);
		return vbox;
	}
	VBox vbox = new VBox();
    final private Map<String, Agenda.AppointmentGroup> appointmentGroupMap = new TreeMap<String, Agenda.AppointmentGroup>();
	private Agenda agenda = null;

	/**
	 * 
	 */
	@Test
	public void renderRegularAppointment()
	{
		TestUtil.runThenWaitForPaintPulse( () -> {
			agenda.appointments().add( new Agenda.AppointmentImpl()
	            .withStartTime(TestUtil.quickParseCalendarFromDateTime("2014-01-01T10:00:00.000"))
	            .withEndTime(TestUtil.quickParseCalendarFromDateTime("2014-01-01T12:00:00.000"))
	            .withAppointmentGroup(appointmentGroupMap.get("group01"))
            );
		});
				
		Node n = (Node)find("#AppointmentRegularBodyPane1");
		//AssertNode.generateSource("n", n, null, false, jfxtras.test.AssertNode.A.XYWH);
		new AssertNode(n).assertXYWH(0.5, 419.5, 125.0, 84.0, 0.01);
		//TestUtil.sleep(3000);
	}

	/**
	 * 
	 */
	@Test
	public void renderWholeDayAppointment()
	{
		TestUtil.runThenWaitForPaintPulse( () -> {
			agenda.appointments().add( new Agenda.AppointmentImpl()
	            .withStartTime(TestUtil.quickParseCalendarFromDateTime("2014-01-01T10:00:00.000"))
	            .withEndTime(TestUtil.quickParseCalendarFromDateTime("2014-01-01T12:00:00.000"))
	            .withAppointmentGroup(appointmentGroupMap.get("group01"))
	            .withWholeDay(true)
            );
		});
			
		{
			Node n = (Node)find("#AppointmentWholedayBodyPane1");
			//AssertNode.generateSource("n", n, null, false, jfxtras.test.AssertNode.A.XYWH);
			new AssertNode(n).assertXYWH(0.5, 0.0, 5.0, 1006.0, 0.01);
		}
		{
			Node n = (Node)find("#AppointmentWholedayHeaderPane1");
			//AssertNode.generateSource("n", n, null, false, jfxtras.test.AssertNode.A.XYWH);
			new AssertNode(n).assertXYWH(0.0, 24.0390625, 135.21763392857142, 20.9609375, 0.01);
		}
		//TestUtil.sleep(3000);
	}

	/**
	 * 
	 */
	@Test
	public void createAppointmentByDragging()
	{
		Assert.assertEquals(0, agenda.appointments().size() );
		
		move("#hourLine10");
		press(MouseButton.PRIMARY);
		move("#hourLine12");
		release(MouseButton.PRIMARY);
		
		Assert.assertEquals(1, agenda.appointments().size() );
		Assert.assertEquals("2014-01-01T10:00:00.000", TestUtil.quickFormatCalendarAsDateTime(agenda.appointments().get(0).getStartTime()) );
		Assert.assertEquals("2014-01-01T12:00:00.000", TestUtil.quickFormatCalendarAsDateTime(agenda.appointments().get(0).getEndTime()) );
		
		find("#AppointmentRegularBodyPane1"); // validate that the pane has the expected id
		//TestUtil.sleep(3000);
	}
	
	/**
	 * 
	 */
	@Test
	public void createWholedayAppointmentByClicking()
	{
		Assert.assertEquals(0, agenda.appointments().size() );
		
		move("#DayHeader2014-01-01");
		press(MouseButton.PRIMARY);
		release(MouseButton.PRIMARY);
		
		Assert.assertEquals(1, agenda.appointments().size() );
		Assert.assertEquals("2014-01-01T00:00:00.000", TestUtil.quickFormatCalendarAsDateTime(agenda.appointments().get(0).getStartTime()) );
		Assert.assertEquals("2014-01-02T00:00:00.000", TestUtil.quickFormatCalendarAsDateTime(agenda.appointments().get(0).getEndTime()) );
		
		assertFind("#AppointmentWholedayBodyPane1");
		assertFind("#AppointmentWholedayHeaderPane1");
		//TestUtil.sleep(3000);
	}

	/**
	 * 
	 */
	@Test
	public void showAppointmentMenu()
	{
		TestUtil.runThenWaitForPaintPulse( () -> {
			agenda.appointments().add( new Agenda.AppointmentImpl()
	            .withStartTime(TestUtil.quickParseCalendarFromDateTime("2014-01-01T10:00:00.000"))
	            .withEndTime(TestUtil.quickParseCalendarFromDateTime("2014-01-01T12:00:00.000"))
	            .withAppointmentGroup(appointmentGroupMap.get("group01"))
            );
		});
				
		click("#AppointmentRegularBodyPane1 .MenuIcon");
		assertPopupIsVisible(find("#AppointmentRegularBodyPane1"));
		//TestUtil.sleep(3000);
	}

	/**
	 * 
	 */
	@Test
	public void deleteAppointmentByMenu()
	{
		TestUtil.runThenWaitForPaintPulse( () -> {
			agenda.appointments().add( new Agenda.AppointmentImpl()
	            .withStartTime(TestUtil.quickParseCalendarFromDateTime("2014-01-01T10:00:00.000"))
	            .withEndTime(TestUtil.quickParseCalendarFromDateTime("2014-01-01T12:00:00.000"))
	            .withAppointmentGroup(appointmentGroupMap.get("group01"))
            );
		});
				
		Assert.assertEquals(1, agenda.appointments().size() );
		click("#AppointmentRegularBodyPane1 .MenuIcon");
		click(".delete-icon");
		Assert.assertEquals(0, agenda.appointments().size() );
		//TestUtil.sleep(3000);
	}

//	/**
//	 *  
//	 */
//	@Test
//	public void removeAppointmentByDeleteKey()
//	{
//		TestUtil.runThenWaitForPaintPulse( () -> {
//			agenda.appointments().add( new Agenda.AppointmentImpl()
//	            .withStartTime(TestUtil.quickParseCalendarFromDateTime("2014-01-01T10:00:00.000"))
//	            .withEndTime(TestUtil.quickParseCalendarFromDateTime("2014-01-01T12:00:00.000"))
//	            .withAppointmentGroup(appointmentGroupMap.get("group01"))
//            );
//		});
//				
//		Assert.assertEquals(1, agenda.appointments().size() );
//		move("#AppointmentRegularBodyPane1"); 
//		press(KeyCode.DELETE);
//		Assert.assertEquals(0, agenda.appointments().size() );
//		TestUtil.sleep(3000);
//	}


	/**
	 * 
	 */
	@Test
	public void renderOverlappingAppointments()
	{
		TestUtil.runThenWaitForPaintPulse( () -> {
			agenda.appointments().add( new Agenda.AppointmentImpl()
	            .withStartTime(TestUtil.quickParseCalendarFromDateTime("2014-01-01T10:00:00.000"))
	            .withEndTime(TestUtil.quickParseCalendarFromDateTime("2014-01-01T12:00:00.000"))
	            .withAppointmentGroup(appointmentGroupMap.get("group01"))
            );
			agenda.appointments().add( new Agenda.AppointmentImpl()
	            .withStartTime(TestUtil.quickParseCalendarFromDateTime("2014-01-01T11:00:00.000"))
	            .withEndTime(TestUtil.quickParseCalendarFromDateTime("2014-01-01T13:00:00.000"))
	            .withAppointmentGroup(appointmentGroupMap.get("group01"))
	        );
		});
				
		{
			Node n = (Node)find("#AppointmentRegularBodyPane1");
			//AssertNode.generateSource("n", n, null, false, jfxtras.test.AssertNode.A.A.XYWH);
			new AssertNode(n).assertXYWH(0.5, 419.5, 110.0, 84.0, 0.01);
		}
		{
			Node n = (Node)find("#AppointmentRegularBodyPane2");
			//AssertNode.generateSource("n", n, null, false, jfxtras.test.AssertNode.A.A.XYWH);
			new AssertNode(n).assertXYWH(62.5, 461.5, 63.0, 84.0, 0.01);
		}
		//TestUtil.sleep(3000);
	}

	/**
	 * 
	 */
	@Test
	public void toggleWholedayInMenu()
	{
		TestUtil.runThenWaitForPaintPulse( () -> {
			agenda.appointments().add( new Agenda.AppointmentImpl()
	            .withStartTime(TestUtil.quickParseCalendarFromDateTime("2014-01-01T10:00:00.000"))
	            .withEndTime(TestUtil.quickParseCalendarFromDateTime("2014-01-01T12:00:00.000"))
	            .withAppointmentGroup(appointmentGroupMap.get("group01"))
            );
		});

		click("#AppointmentRegularBodyPane1 .MenuIcon");
		click("#wholeday-checkbox");
		click(".close-icon");
		Assert.assertEquals(1, agenda.appointments().size() );
		Assert.assertEquals(true, agenda.appointments().get(0).isWholeDay().booleanValue() );

		// this not only checks if the appointment was changed, but also if it was rerendered
		{
			Node n = (Node)find("#AppointmentWholedayBodyPane1");
			//AssertNode.generateSource("n", n, null, false, jfxtras.test.AssertNode.A.XYWH);
			new AssertNode(n).assertXYWH(0.5, 0.0, 5.0, 1006.0, 0.01);
		}
		{
			Node n = (Node)find("#AppointmentWholedayHeaderPane1");
			//AssertNode.generateSource("n", n, null, false, jfxtras.test.AssertNode.A.XYWH);
			new AssertNode(n).assertXYWH(0.0, 24.0390625, 135.21763392857142, 20.9609375, 0.01);
		}
		//TestUtil.sleep(3000);
	}

	/**
	 * 
	 */
	@Test
	public void dragRegularAppointment()
	{
		TestUtil.runThenWaitForPaintPulse( () -> {
			agenda.appointments().add( new Agenda.AppointmentImpl()
	            .withStartTime(TestUtil.quickParseCalendarFromDateTime("2014-01-01T10:00:00.000"))
	            .withEndTime(TestUtil.quickParseCalendarFromDateTime("2014-01-01T12:00:00.000"))
	            .withAppointmentGroup(appointmentGroupMap.get("group01"))
            );
		});
		assertFind("#AppointmentRegularBodyPane1"); 
				
		move("#hourLine11"); // the pane is beneath the mouse now since it runs from 10 to 12
		press(MouseButton.PRIMARY);
		move("#hourLine15");
		release(MouseButton.PRIMARY);
		
		Assert.assertEquals(1, agenda.appointments().size() );
		Assert.assertEquals("2014-01-01T14:00:00.000", TestUtil.quickFormatCalendarAsDateTime(agenda.appointments().get(0).getStartTime()) );
		Assert.assertEquals("2014-01-01T16:00:00.000", TestUtil.quickFormatCalendarAsDateTime(agenda.appointments().get(0).getEndTime()) );
		//TestUtil.sleep(3000);
	}
	
	/**
	 * 
	 */
	@Test
	public void dragRegularAppointmentToNextDay()
	{
		TestUtil.runThenWaitForPaintPulse( () -> {
			agenda.appointments().add( new Agenda.AppointmentImpl()
	            .withStartTime(TestUtil.quickParseCalendarFromDateTime("2014-01-01T10:00:00.000"))
	            .withEndTime(TestUtil.quickParseCalendarFromDateTime("2014-01-01T12:00:00.000"))
	            .withAppointmentGroup(appointmentGroupMap.get("group01"))
            );
		});
		assertFind("#AppointmentRegularBodyPane1");
				
		move("#hourLine11"); // the pane is beneath the mouse now since it runs from 10 to 12
		press(MouseButton.PRIMARY);
		moveBy(100, 0);
		release(MouseButton.PRIMARY);
		
		Assert.assertEquals(1, agenda.appointments().size() );
		Assert.assertEquals("2014-01-02T10:00:00.000", TestUtil.quickFormatCalendarAsDateTime(agenda.appointments().get(0).getStartTime()) );
		Assert.assertEquals("2014-01-02T12:00:00.000", TestUtil.quickFormatCalendarAsDateTime(agenda.appointments().get(0).getEndTime()) );
		//TestUtil.sleep(3000);
	}

	/**
	 * 
	 */
	@Test
	public void extendAppointmentByDragging()
	{
		TestUtil.runThenWaitForPaintPulse( () -> {
			agenda.appointments().add( new Agenda.AppointmentImpl()
	            .withStartTime(TestUtil.quickParseCalendarFromDateTime("2014-01-01T10:00:00.000"))
	            .withEndTime(TestUtil.quickParseCalendarFromDateTime("2014-01-01T12:00:00.000"))
	            .withAppointmentGroup(appointmentGroupMap.get("group01"))
            );
		});
				
		move("#AppointmentRegularBodyPane1 .DurationDragger"); 
		press(MouseButton.PRIMARY);
		move("#hourLine15");
		release(MouseButton.PRIMARY);
		
		Assert.assertEquals(1, agenda.appointments().size() );
		Assert.assertEquals("2014-01-01T10:00:00.000", TestUtil.quickFormatCalendarAsDateTime(agenda.appointments().get(0).getStartTime()) );
		Assert.assertEquals("2014-01-01T15:00:00.000", TestUtil.quickFormatCalendarAsDateTime(agenda.appointments().get(0).getEndTime()) );
		//TestUtil.sleep(3000);
	}


	/**
	 * 
	 */
	@Test
	public void dragWholeDayToOutside()
	{
		TestUtil.runThenWaitForPaintPulse( () -> {
			agenda.appointments().add( new Agenda.AppointmentImpl()
	            .withStartTime(TestUtil.quickParseCalendarFromDateTime("2014-01-01T10:00:00.000"))
	            .withEndTime(TestUtil.quickParseCalendarFromDateTime("2014-01-01T12:00:00.000"))
	            .withWholeDay(true)
	            .withAppointmentGroup(appointmentGroupMap.get("group01"))
            );
		});
		
		// make sure the two nodes exist 
		assertFind("#AppointmentWholedayBodyPane1");
		assertFind("#AppointmentWholedayHeaderPane1");
		
		move("#AppointmentWholedayBodyPane1"); 
		press(MouseButton.PRIMARY);
		move(0, 0);
		release(MouseButton.PRIMARY);
		
		// nothing changed 
		Assert.assertEquals(1, agenda.appointments().size() );
		assertFind("#AppointmentWholedayBodyPane1");
		assertFind("#AppointmentWholedayHeaderPane1");
		// TestUtil.sleep(3000);
	}

	/**
	 * 
	 */
	@Test
	public void dragWholeDayToBody()
	{
		TestUtil.runThenWaitForPaintPulse( () -> {
			agenda.appointments().add( new Agenda.AppointmentImpl()
	            .withStartTime(TestUtil.quickParseCalendarFromDateTime("2014-01-01T00:00:00.000"))
	            .withEndTime(TestUtil.quickParseCalendarFromDateTime("2014-01-01T12:00:00.000"))
	            .withWholeDay(true)
	            .withAppointmentGroup(appointmentGroupMap.get("group01"))
            );
		});
		
		// make sure the two nodes exist 
		assertFind("#AppointmentWholedayBodyPane1");
		assertFind("#AppointmentWholedayHeaderPane1");
		
		// drag from header to body
		move("#AppointmentWholedayHeaderPane1"); 
		press(MouseButton.PRIMARY);
		move("#hourLine10");
		release(MouseButton.PRIMARY);
		
		// now there should be a regular appointment
		assertFind("#AppointmentRegularBodyPane1");
		Assert.assertEquals(1, agenda.appointments().size() );
		Assert.assertEquals("2014-01-01T00:00:00.000", TestUtil.quickFormatCalendarAsDateTime(agenda.appointments().get(0).getStartTime()) );
		Assert.assertEquals("2014-01-02T00:00:00.000", TestUtil.quickFormatCalendarAsDateTime(agenda.appointments().get(0).getEndTime()) );
		//TestUtil.sleep(3000);
	}

	/**
	 * 
	 */
	@Test
	public void dragWholeDayTaskToBody()
	{
		TestUtil.runThenWaitForPaintPulse( () -> {
			agenda.appointments().add( new Agenda.AppointmentImpl()
	            .withStartTime(TestUtil.quickParseCalendarFromDateTime("2014-01-01T00:00:00.000"))
	            .withWholeDay(true)
	            .withAppointmentGroup(appointmentGroupMap.get("group01"))
            );
		});
		
		// make sure the two nodes exist 
		assertFind("#AppointmentWholedayBodyPane1");
		assertFind("#AppointmentWholedayHeaderPane1");
		
		// drag from header to body
		move("#AppointmentWholedayHeaderPane1"); 
		press(MouseButton.PRIMARY);
		move("#hourLine10");
		release(MouseButton.PRIMARY);
		
		// now there should be a regular appointment
		assertFind("#AppointmentTaskBodyPane1");
		Assert.assertEquals(1, agenda.appointments().size() );
		Assert.assertEquals("2014-01-01T10:00:00.000", TestUtil.quickFormatCalendarAsDateTime(agenda.appointments().get(0).getStartTime()) );
		Assert.assertNull(agenda.appointments().get(0).getEndTime());
		//TestUtil.sleep(3000);
	}

	/**
	 * 
	 */
	@Test
	public void dragRegularToHeader()
	{
		TestUtil.runThenWaitForPaintPulse( () -> {
			agenda.appointments().add( new Agenda.AppointmentImpl()
	            .withStartTime(TestUtil.quickParseCalendarFromDateTime("2014-01-01T01:00:00.000"))
	            .withEndTime(TestUtil.quickParseCalendarFromDateTime("2014-01-01T12:00:00.000"))
	            .withAppointmentGroup(appointmentGroupMap.get("group01"))
            );
		});
		
		// make sure the node exist 
		assertFind("#AppointmentRegularBodyPane1");
		
		// drag from header to body
		move("#AppointmentRegularBodyPane1"); 
		press(MouseButton.PRIMARY);
		move("#DayHeader2014-01-02"); // header of next day
		release(MouseButton.PRIMARY);
		
		// now there should be a regular appointment
		assertFind("#AppointmentWholedayBodyPane1");
		assertFind("#AppointmentWholedayHeaderPane1");
		Assert.assertEquals(1, agenda.appointments().size() );
		Assert.assertEquals("2014-01-02T01:00:00.000", TestUtil.quickFormatCalendarAsDateTime(agenda.appointments().get(0).getStartTime()) );
		Assert.assertTrue(agenda.appointments().get(0).isWholeDay());
		//TestUtil.sleep(3000);
	}

	/**
	 * 
	 */
	@Test
	public void dragTaskToHeader()
	{
		TestUtil.runThenWaitForPaintPulse( () -> {
			agenda.appointments().add( new Agenda.AppointmentImpl()
	            .withStartTime(TestUtil.quickParseCalendarFromDateTime("2014-01-01T01:00:00.000"))
	            .withAppointmentGroup(appointmentGroupMap.get("group01"))
            );
		});
		
		// make sure the node exist 
		assertFind("#AppointmentTaskBodyPane1");
		
		// drag from header to body
		move("#AppointmentTaskBodyPane1"); 
		press(MouseButton.PRIMARY);
		move("#DayHeader2014-01-02"); // header of next day
		release(MouseButton.PRIMARY);
		
		// now there should be a regular appointment
		assertFind("#AppointmentWholedayBodyPane1");
		assertFind("#AppointmentWholedayHeaderPane1");
		Assert.assertEquals(1, agenda.appointments().size() );
		Assert.assertEquals("2014-01-02T01:00:00.000", TestUtil.quickFormatCalendarAsDateTime(agenda.appointments().get(0).getStartTime()) );
		Assert.assertTrue(agenda.appointments().get(0).isWholeDay());
		//TestUtil.sleep(3000);
	}

	/**
	 * 
	 */
	@Test
	public void selectSingle()
	{
		// given
		TestUtil.runThenWaitForPaintPulse( () -> {
			agenda.appointments().add( new Agenda.AppointmentImpl()
	            .withStartTime(TestUtil.quickParseCalendarFromDateTime("2014-01-01T10:00:00.000"))
	            .withEndTime(TestUtil.quickParseCalendarFromDateTime("2014-01-01T12:00:00.000"))
	            .withAppointmentGroup(appointmentGroupMap.get("group01"))
            );
		});
		Assert.assertEquals(1, agenda.appointments().size() );
		Assert.assertEquals(0, agenda.selectedAppointments().size() );
		
		// when
		click("#AppointmentRegularBodyPane1");
		
		// then
		Assert.assertEquals(1, agenda.appointments().size() );
		Assert.assertEquals(1, agenda.selectedAppointments().size() );
		
		// when
		click("#hourLine15");
		
		// then
		Assert.assertEquals(1, agenda.appointments().size() );
		Assert.assertEquals(0, agenda.selectedAppointments().size() );
		//TestUtil.sleep(3000);
	}

	/**
	 * 
	 */
	@Test
	public void selectShift()
	{
		// given
		TestUtil.runThenWaitForPaintPulse( () -> {
			agenda.appointments().add( new Agenda.AppointmentImpl()
	            .withStartTime(TestUtil.quickParseCalendarFromDateTime("2014-01-01T07:00:00.000"))
	            .withEndTime(TestUtil.quickParseCalendarFromDateTime("2014-01-01T08:00:00.000"))
	            .withAppointmentGroup(appointmentGroupMap.get("group01"))
            );
			agenda.appointments().add( new Agenda.AppointmentImpl()
	            .withStartTime(TestUtil.quickParseCalendarFromDateTime("2014-01-01T10:00:00.000"))
	            .withEndTime(TestUtil.quickParseCalendarFromDateTime("2014-01-01T12:00:00.000"))
	            .withAppointmentGroup(appointmentGroupMap.get("group01"))
	        );
		});
		Assert.assertEquals(2, agenda.appointments().size() );
		Assert.assertEquals(0, agenda.selectedAppointments().size() );
		
		// when
		click("#AppointmentRegularBodyPane1"); // select first
		
		// then
		Assert.assertEquals(2, agenda.appointments().size() );
		Assert.assertEquals(1, agenda.selectedAppointments().size() );
		
		// when
		click("#AppointmentRegularBodyPane2"); // select second
		
		// then
		Assert.assertEquals(2, agenda.appointments().size() );
		Assert.assertEquals(1, agenda.selectedAppointments().size() );
		
		// when
		press(KeyCode.SHIFT);
		click("#AppointmentRegularBodyPane1"); // select both
		release(KeyCode.SHIFT);
		
		// then
		Assert.assertEquals(2, agenda.appointments().size() );
		Assert.assertEquals(2, agenda.selectedAppointments().size() );
		
		// when
		press(KeyCode.SHIFT);
		click("#AppointmentRegularBodyPane1"); // select again (no change)
		release(KeyCode.SHIFT);
		
		// then
		Assert.assertEquals(2, agenda.appointments().size() );
		Assert.assertEquals(2, agenda.selectedAppointments().size() );
		
		// when
		click("#hourLine15");
		
		// then
		Assert.assertEquals(2, agenda.appointments().size() );
		Assert.assertEquals(0, agenda.selectedAppointments().size() );
		//TestUtil.sleep(3000);
	}

	/**
	 * 
	 */
	@Test
	public void selectControl()
	{
		// given
		TestUtil.runThenWaitForPaintPulse( () -> {
			agenda.appointments().add( new Agenda.AppointmentImpl()
	            .withStartTime(TestUtil.quickParseCalendarFromDateTime("2014-01-01T07:00:00.000"))
	            .withEndTime(TestUtil.quickParseCalendarFromDateTime("2014-01-01T08:00:00.000"))
	            .withAppointmentGroup(appointmentGroupMap.get("group01"))
            );
			agenda.appointments().add( new Agenda.AppointmentImpl()
	            .withStartTime(TestUtil.quickParseCalendarFromDateTime("2014-01-01T10:00:00.000"))
	            .withEndTime(TestUtil.quickParseCalendarFromDateTime("2014-01-01T12:00:00.000"))
	            .withAppointmentGroup(appointmentGroupMap.get("group01"))
	        );
		});
		Assert.assertEquals(2, agenda.appointments().size() );
		Assert.assertEquals(0, agenda.selectedAppointments().size() );
		
		// when
		click("#AppointmentRegularBodyPane1"); // select first
		
		// then
		Assert.assertEquals(2, agenda.appointments().size() );
		Assert.assertEquals(1, agenda.selectedAppointments().size() );
		
		// when
		press(KeyCode.CONTROL);
		click("#AppointmentRegularBodyPane2"); // select second
		release(KeyCode.CONTROL);
		
		// then
		Assert.assertEquals(2, agenda.appointments().size() );
		Assert.assertEquals(2, agenda.selectedAppointments().size() );
		
		// when
		press(KeyCode.CONTROL);
		click("#AppointmentRegularBodyPane2"); // select again (deselects)
		release(KeyCode.CONTROL);
		
		// then
		Assert.assertEquals(2, agenda.appointments().size() );
		Assert.assertEquals(1, agenda.selectedAppointments().size() );
		
		// when
		press(KeyCode.CONTROL);
		click("#AppointmentRegularBodyPane1"); // select again (deselects)
		release(KeyCode.CONTROL);
		
		// then
		Assert.assertEquals(2, agenda.appointments().size() );
		Assert.assertEquals(0, agenda.selectedAppointments().size() );
		//TestUtil.sleep(3000);
	}

	// TODO: create new wholeday appointment by clicking in the header
	// TODO: drag from header to day and vice versa
	// TODO: select
	// TODO: default locale EN, assert first day of week
	// TODO: set locale to DE, determine first day of week
}
