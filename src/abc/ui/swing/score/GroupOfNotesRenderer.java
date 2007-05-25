package abc.ui.swing.score;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Stroke;
import java.awt.geom.Point2D;
import java.util.Vector;

import abc.notation.Note;

public class GroupOfNotesRenderer {
	
	public static double render(ScoreRenditionContext context, Point2D base, Note[] notes){
		double cursorPosOffset = 0;
		Note highestNote = Note.getHighestNote(notes);
		BasicStroke notesLinkStroke = context.getNotesLinkStroke();
		BasicStroke stemStroke = context.getStemStroke();
		SNotePartOfGroup sn = new SNotePartOfGroup(highestNote, base, context);
		int stemYend = sn.getStemYBegin()-context.getStemLength();
		Stroke defaultStroke = context.getGraphics().getStroke();
		Vector sNoteInstances = new Vector();
		for (int i=0; i<notes.length; i++) {
			short noteStrictDuration = notes[i].getStrictDuration();
			if (noteStrictDuration==Note.THIRTY_SECOND || noteStrictDuration==Note.SIXTEENTH || noteStrictDuration==Note.EIGHTH
					|| noteStrictDuration==Note.QUARTER){
				SNotePartOfGroup n = new SNotePartOfGroup(notes[i], base, context);
				sNoteInstances.addElement(n);
				n.setStemYEnd(stemYend);
				int stemX = n.getStemX();
				int width = n.render(context, base);
				width+=context.getNotesSpacing();
				base.setLocation(base.getX()+width, base.getY());
				//context.getGraphics().drawLine(stemX, stemYbegin, stemX, stemYend);
				//===== draw rhythm info
				context.getGraphics().setStroke(notesLinkStroke);
				short[] slowerRhythms = null;
				switch (noteStrictDuration) {
					case Note.EIGHTH : slowerRhythms = new short[1]; slowerRhythms[0] = Note.EIGHTH; break;
					case Note.SIXTEENTH : slowerRhythms = new short[2]; slowerRhythms[0] = Note.EIGHTH; slowerRhythms[1] = Note.SIXTEENTH; break;
					case Note.THIRTY_SECOND: slowerRhythms = new short[3]; slowerRhythms[0] = Note.EIGHTH; slowerRhythms[1] = Note.SIXTEENTH; slowerRhythms[2] = Note.THIRTY_SECOND; break;
				}
				for (int j=0; j<slowerRhythms.length; j++) {
					//decide where the end of the rhythm is.
					int noteLinkY = -1;
					if (slowerRhythms[j]==Note.EIGHTH)
						noteLinkY = (int)(stemYend+notesLinkStroke.getLineWidth()/2.5);
					else
						if (slowerRhythms[j]==Note.SIXTEENTH)
							noteLinkY = (int)(stemYend+notesLinkStroke.getLineWidth()*2);
						else
							if (slowerRhythms[j]==Note.THIRTY_SECOND)
								noteLinkY = (int)(stemYend+notesLinkStroke.getLineWidth()*3.5);
					
					int noteLinkEnd = -1;
					if (i==0)
						noteLinkEnd = (int)(stemX+context.getNoteWidth()/2);
					else
						if (notes[i-1].getStrictDuration()<=slowerRhythms[j])
							//the end is the stem of the previous note.
							noteLinkEnd = ((SNotePartOfGroup)sNoteInstances.elementAt((i-1))).getStemX();//getE (int)(stemX-2*context.getNoteWidth()); 
						else
							noteLinkEnd = (int)(stemX-context.getNoteWidth()/2);
					context.getGraphics().drawLine(stemX, noteLinkY, noteLinkEnd, noteLinkY);
				}
				//restore defaut stroke.
				context.getGraphics().setStroke(defaultStroke);
				/*width = (int)(SingleNoteRenderer.getAccidentalRenditionWidth(context, base, notes[i])
					+ context.getNoteWidth() + SingleNoteRenderer.getOffsetAfterNoteRendition(context)) ;
				//update cursor pos*/
				cursorPosOffset+=width;
			}
		}
		//context.getGraphics().setStroke(notesLinkStroke);
		//context.getGraphics().drawLine((int)startNoteX, (int)(stemYend+notesLinkStroke.getLineWidth()/2), (int)endNoteX, (int)(stemYend+notesLinkStroke.getLineWidth()/2));
		//int noteX = (int)(SingleNoteRenderer.getNoteX(context, base, highestNote)+cursorPosOffset);
		//int noteY = (int)(SingleNoteRenderer.getNoteY(context, base, highestNote) - context.getNoteHeigth()*4);
		/*switch (highestNote.getStrictDuration()) {
			case Note.THIRTY_SECOND : noteY+=context.getNoteHeigth()*0.5;break;
			case Note.SIXTEENTH : noteY+=context.getNoteHeigth()*0.5;break;
			case Note.WHOLE: break;
		}
		context.getGraphics().drawChars(ScoreRenditionContext.NOTE, 0, 1,noteX, noteY);*/
		//context.getGraphics().drawLine((int)(base.getX()+cursorPosOffset), (int)base.getY(), (int)(base.getX()+cursorPosOffset), (int)(base.getY()-50));
		//context.getGraphics().setColor(Color.BLACK);
		return cursorPosOffset;
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
		//System.out.println("offset for " + note +"," + note.getOctaveTransposition() + " : " + positionOffset);
		return positionOffset;
	}
}