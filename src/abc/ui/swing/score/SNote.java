package abc.ui.swing.score;

import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Point2D;

import abc.notation.AccidentalType;
import abc.notation.Note;

public class SNote {
	protected Note note = null;
	protected Point2D notePosition = null;
	protected Point2D accidentalsPosition = null;
	protected char[] noteChars = null;
	protected char[] accidentalsChars = null;
	protected  int width = 0;
	
	public SNote(Note noteValue, Point2D base, ScoreRenditionContext c) {
		note = noteValue;
		int noteY = (int)(base.getY()-getOffset(note)*c.getNoteHeigth());
		double accidentalsWidth = 0;
		if (note.getAccidental()!=AccidentalType.NONE) {
			accidentalsPosition = new Point2D.Double(base.getX(),noteY-c.getNoteHeigth()/2);
			switch (note.getAccidental()) {
				case AccidentalType.FLAT: accidentalsChars = ScoreRenditionContext.FLAT; 
					accidentalsWidth = c.getFlatBounds().getWidth(); 
					break;
				case AccidentalType.SHARP: accidentalsChars = ScoreRenditionContext.SHARP; 
					accidentalsWidth = c.getSharpBounds().getWidth(); 
					break;
				case AccidentalType.NATURAL: accidentalsChars = ScoreRenditionContext.NATURAL; 
					accidentalsWidth = c.getNaturalBounds().getWidth(); 
					break;
			}
		}
		short noteDuration = note.getStrictDuration();
		switch (noteDuration) {
				//Note.SIXTY_FOURTH: chars[0] = ScoreRenditionContext.
				case Note.THIRTY_SECOND : noteChars = ScoreRenditionContext.THIRTY_SECOND_NOTE; break;
				case Note.SIXTEENTH : noteChars = ScoreRenditionContext.SIXTEENTH_NOTE; break;
				case Note.EIGHTH : noteChars = ScoreRenditionContext.EIGHTH_NOTE; break;
				case Note.QUARTER: noteChars = ScoreRenditionContext.QUARTER_NOTE; break;
				case Note.HALF: noteChars = ScoreRenditionContext.HALF_NOTE; break;
				case Note.WHOLE: noteChars = ScoreRenditionContext.WHOLE_NOTE; break;
			}
			//double noteY =(int)(base.getY()-getOffset(note)*c.getNoteHeigth());
			double noteX = base.getX()+accidentalsWidth;
			notePosition = new Point2D.Double(noteX, noteY);
			//c.getGraphics().drawChars(chars, 0, chars.length, noteX, noteY);
			/*if (note.getHeight()==Note.C || note.getHeight()==Note.a)
				c.getGraphics().drawChars(ScoreRenditionContext.STROKE, 0, 1, (int)(noteX-context.getNoteWidth()/4), strokeY);*/
			width = (int)(accidentalsWidth+c.getNoteWidth());
			/*if (note.countDots()!=0){
				renderDots(c, base, note);
			}*/
			//context.getGraphics().setColor(Color.RED);
			//context.getGraphics().drawLine((int)(base.getX()+totalWidth), (int)base.getY(), (int)(base.getX()+totalWidth), (int)(base.getY()-50));
			//context.getGraphics().setColor(Color.BLACK);
			//return -1;
			/*if (note.getAccidental()!=AccidentalType.NONE) {
				accidentalsPosition = new Point2D.Double(base.getX(),0);
				if (note.getAccidental()==AccidentalType.FLAT)
					chars = ScoreRenditionContext.FLAT;
				else
					if (note.getAccidental()==AccidentalType.SHARP)
						chars = ScoreRenditionContext.SHARP; 
					else
						if (note.getAccidental()==AccidentalType.NATURAL)
						chars = ScoreRenditionContext.NATURAL;
					context.getGraphics().drawChars(chars, 0, chars.length, 
						(int)(base.getX()), 
						(int)(noteY-context.getNoteHeigth()/2));
				}*/
		}
	
	public static double getOffset(Note note) {
		double positionOffset = 0;
		byte noteHeight = note.getStrictHeight();
		switch (noteHeight) {
			case Note.C : positionOffset = -1.5; break;
			case Note.D : positionOffset = -1;break;
			case Note.E : positionOffset = -0.5;break;
			case Note.F : positionOffset = 0;break;
			case Note.G : positionOffset = 0.5;break;
			case Note.A : positionOffset = 1;break;
			case Note.B : positionOffset = 1.5;break;
		}
		positionOffset = positionOffset + note.getOctaveTransposition()*3.5;
		short noteDuration = note.getStrictDuration();
		switch (noteDuration) {
			case Note.THIRTY_SECOND : 
			case Note.SIXTEENTH : 
			case Note.WHOLE: positionOffset=positionOffset+0.5; break;
		}
		//System.out.println("offset : " + positionOffset );
		return positionOffset;
	}
	
	public Point2D getNotePosition(){
		return notePosition;
	}
		
	public Point2D getAccidentalsPosition(ScoreRenditionContext context, Point2D base, Note note){
		return accidentalsPosition;
	}
	
	public int render(ScoreRenditionContext context, Point2D base){
		renderExtendedStaffLines(context, base);
		context.getGraphics().drawChars(noteChars, 0, 1, (int)notePosition.getX(), (int)notePosition.getY());
		return width;
	}
	
	protected void renderAccidentals(Graphics2D gfx) {
		gfx.drawChars(accidentalsChars, 0, 1, (int)accidentalsPosition.getX(), (int)accidentalsPosition.getY()); 
	}
	
	public void renderExtendedStaffLines(ScoreRenditionContext c, Point2D base){
		if (note.getHeight()<=Note.C){
			double currentOffset = getOffset(new Note(Note.C, AccidentalType.NONE));
			int currentPosition = (int)(base.getY()-currentOffset*c.getNoteHeigth()/1.5);
			double offset = getOffset(note);
			Stroke dfs = c.getGraphics().getStroke();
			c.getGraphics().setStroke(c.getStemStroke());
			while (currentOffset>=offset) {
				c.getGraphics().drawLine(
						(int)(notePosition.getX()-5), currentPosition,
						(int)(notePosition.getX()+c.getNoteWidth()+5), currentPosition);
				currentOffset--;
				currentPosition = (int)(currentPosition + c.getNoteHeigth());
				System.out.println("current offset : " + currentOffset + " " + currentPosition);
			}
			c.getGraphics().setStroke(dfs);
		}
		else
			if (note.getHeight()>=Note.a){
				double currentOffset = getOffset(new Note(Note.a, AccidentalType.NONE));
				int currentPosition = (int)(base.getY()-currentOffset*c.getNoteHeigth()-c.getNoteHeigth()/2);
				double offset = getOffset(note);
				Stroke dfs = c.getGraphics().getStroke();
				c.getGraphics().setStroke(c.getStemStroke());
				while (currentOffset<=offset) {
					c.getGraphics().drawLine(
							(int)(notePosition.getX()-5), currentPosition,
							(int)(notePosition.getX()+c.getNoteWidth()+5), currentPosition);
					currentOffset++;
					currentPosition = (int)(currentPosition - c.getNoteHeigth());
					System.out.println("current offset : " + currentOffset + " " + currentPosition);
				}
				c.getGraphics().setStroke(dfs);
			}
	}
		/*public static void renderDots(ScoreRenditionContext context, Point2D base, Note note){
			int noteY = getNoteY(context, base, note);
			int dotY = (int)(noteY-context.getNoteHeigth()/6);
			if (note.countDots()!=0){
				context.getGraphics().drawChars(ScoreRenditionContext.DOT, 0, 1, 
					(int)(base.getX()
						+context.getNoteWidth()+((note.getAccidental()!=AccidentalType.NONE)?context.getNoteWidth():0)), 
						dotY);
			}
		}
		
		}*/
		
}


