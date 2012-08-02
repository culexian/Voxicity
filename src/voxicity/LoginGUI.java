/*
 * Copyright 2011, Erik Lund
 *
 * This file is part of Voxicity.
 *
 *  Voxicity is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Voxicity is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Voxicity.  If not, see <http://www.gnu.org/licenses/>.
 */

package voxicity;

import de.matthiasmann.twl.*;

public class LoginGUI extends Widget
{
	Button login_button = new Button( "LOGIN" );
	Label name_label = new Label( "Hostname:" );
	EditField name_field = new EditField();
	DialogLayout panel;

	boolean login_pressed = false;

	public LoginGUI()
	{
		name_field.setText( "culex.no-ip.org" );

		panel = new DialogLayout();
		panel.setTheme( "login-panel" );
		add( panel );
		panel.setSize( 300, 200 );

		DialogLayout.Group v_name = panel.createParallelGroup( name_label, name_field );
		DialogLayout.Group v_button = panel.createSequentialGroup( login_button );

		DialogLayout.Group v_group = panel.createSequentialGroup( v_name, v_button );
		panel.setVerticalGroup( v_group );

		DialogLayout.Group h_name = panel.createSequentialGroup( name_label, name_field );
		DialogLayout.Group h_button = panel.createSequentialGroup( login_button );

		DialogLayout.Group h_group = panel.createParallelGroup( h_name, h_button );
		panel.setHorizontalGroup( h_group );

		name_field.setTooltipContent( "Enter the hostname of the server to login to here, either an IP address or a DNS name will suffice." );

		login_button.addCallback( new Runnable()
		{
			public void run()
			{
				login_pressed = true;
			}
		});
	}

	protected void layout()
	{
		panel.adjustSize();
		panel.setPosition( getInnerX() + ( getInnerWidth() - panel.getWidth() ) / 2,
		                   getInnerY() + ( getInnerHeight() - panel.getHeight() ) / 2 );
	}

	public boolean is_login_pressed()
	{
		return login_pressed;
	}

	public String get_server_name()
	{
		return name_field.getText();
	}
}
