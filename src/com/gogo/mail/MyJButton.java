package com.gogo.mail;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;

public class MyJButton extends JButton
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MyJButton(String text)
	{
		super(text);
		setFocusPainted(false);
		setBackground(Color.WHITE);
		setBorder(BorderFactory.createCompoundBorder(null,
				BorderFactory.createEmptyBorder(5, 10, 5, 10)));
		addMouseListener(new MouseAdapter()
		{
			public void mouseEntered(MouseEvent me)
			{
				setCursor(new Cursor(Cursor.HAND_CURSOR));
				setBackground(new Color(225,225,225));
			}
			public void mouseExited(MouseEvent me)
			{
				setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
				setBackground(Color.WHITE);
			}
		});
	}
}
