package grid;

import java.util.ArrayList;
import javafx.scene.input.MouseEvent;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeType;
import javafx.util.Duration;

public class Grid {

	private GridEnums _currMode;
	private boolean _reset;
	private Pane _pane;
	private Timeline _timeline;
	private Rectangle _border;
	private boolean _triggered;
	private ArrayList<Rectangle> _regions;
	private Rectangle _selectedRegion;
	private Color _currColor;
	private PaneOrganizer _po;
	private HelpBox _hb;

	public Grid(Pane pane, PaneOrganizer po) {
		_po = po;
		_currColor = Color.TRANSPARENT;
		_regions = new ArrayList<Rectangle>();
		_reset = true;
		_pane = pane;
		_triggered = false;
		_currMode = GridEnums.STOP;
		this.setupTimeline();
		_pane.addEventHandler(MouseEvent.ANY, new MouseHandler());
		_hb = new HelpBox(_pane);
	}

	private void installBorder() {
		_border = new Rectangle(0.0, 0.0, Constants.ROOT_WIDTH, _po.getRootHeight());
		_border.setStrokeType(StrokeType.INSIDE);
		_border.setStrokeWidth(4);
		_border.setStroke(Color.BLACK);
		_border.setFill(Color.TRANSPARENT);
		_pane.getChildren().add(_border);
	}

	private void setupTimeline() {
		KeyFrame kf = new KeyFrame(Duration.seconds(Constants.DURATION), new TimeHandler());
		_timeline = new Timeline(kf);
		_timeline.setCycleCount(Animation.INDEFINITE);
		_timeline.play();
	}

	public void reset() {
		for (int i = 0; i < _regions.size(); i++) {
			_pane.getChildren().remove(_regions.get(i));
		}
		_regions.clear();
		_reset = true;
		_triggered = false;
	}

	public void setMode(GridEnums mode) {
		_currMode = mode;
	}

	public void generateGrid(Rectangle region) {
		if (_reset) {
			// if the region is wider and taller than than half the initial canvas width and
			// taller
			ArrayList<Rectangle> newRegions;
			if (region.getWidth() > Constants.ROOT_WIDTH / 2 && region.getHeight() > _po.getRootHeight() / 2) {
				newRegions = this.split(GridEnums.HORIZONTAL, GridEnums.VERTICAL, region);
				for (int i = 0; i < newRegions.size(); i++) {
					this.generateGrid(newRegions.get(i));
				}
				return;
			} else if (region.getWidth() > Constants.ROOT_WIDTH / 2) {
				// split vertically
				newRegions = this.split(GridEnums.HORIZONTAL, null, region);
				for (int i = 0; i < newRegions.size(); i++) {
					this.generateGrid(newRegions.get(i));
				}
				return;
			} else if (region.getHeight() > _po.getRootHeight() / 2) {
				// split horizontally
				newRegions = this.split(null, GridEnums.VERTICAL, region);
				for (int i = 0; i < newRegions.size(); i++) {
					this.generateGrid(newRegions.get(i));
				}
				return;
			} else {
				/*
				 * generate two random pivots to determine if the region is large enough to
				 * split vertically and or horizontally (between 120 and 1.5*width or
				 * 1.5*height)
				 */
				double hRand = 120 + Math.random() * Math.abs(region.getWidth() * 1.5 - 120);
				double vRand = 120 + Math.random() * Math.abs(region.getHeight() * 1.5 - 120);
				if (region.getWidth() > hRand && region.getHeight() > vRand) {
					newRegions = this.split(GridEnums.HORIZONTAL, GridEnums.VERTICAL, region);
					for (int i = 0; i < newRegions.size(); i++) {
						this.generateGrid(newRegions.get(i));
					}
					return;
				} else if (region.getWidth() > hRand) {
					// split vertically
					newRegions = this.split(GridEnums.HORIZONTAL, null, region);
					for (int i = 0; i < newRegions.size(); i++) {
						this.generateGrid(newRegions.get(i));
					}
					return;
				} else if (region.getHeight() > vRand) {
					// split horizontally
					newRegions = this.split(null, GridEnums.VERTICAL, region);
					for (int i = 0; i < newRegions.size(); i++) {
						this.generateGrid(newRegions.get(i));
					}
					return;
				} else {
					// fill the region transparently, pink, or blue
					return;
				}
			}
		}
	}

	private ArrayList<Rectangle> split(GridEnums horizontal, GridEnums vertical, Rectangle region) {
		ArrayList<Rectangle> newRegions = new ArrayList<Rectangle>();
		// h is the point on the hlength of region to split at
		// v is the point on the vlength of region to split at
		double h = this.locateSplit(region.getX(), region.getX() + region.getWidth());
		double v = this.locateSplit(region.getY(), region.getY() + region.getHeight());
		if (horizontal != null) {
			if (vertical != null) {
				// split horizontally and vertically

				// r1 has width h - region.getX()
				// r1 has x coord region.getX()
				// r1 has height v - region.getY()
				// r1 has y coord region.getY()
				Rectangle r1 = new Rectangle(region.getX(), region.getY(), h - region.getX(), v - region.getY());
				newRegions.add(r1);

				// r2 has width (region.getX()+region.getWidth()) - h
				// r2 has x coord h
				// r2 has y coord region.getY()
				// r2 has height v - region.getY()
				Rectangle r2 = new Rectangle(h, region.getY(), region.getX() + region.getWidth() - h,
						v - region.getY());
				newRegions.add(r2);

				// r3 has x coord region.getX()
				// r3 has y coord v
				// r3 has width h - region.getX()
				// r3 has height (region.getY() + region.getHeight()) - v
				Rectangle r3 = new Rectangle(region.getX(), v, h - region.getX(),
						region.getY() + region.getHeight() - v);
				newRegions.add(r3);

				// r4 has x coord h
				// r4 has y coord v
				// r4 has width region.getX() + region.getWidth() - h
				// r4 has height region.getY() + region.getHeight() - v
				Rectangle r4 = new Rectangle(h, v, region.getX() + region.getWidth() - h,
						region.getY() + region.getHeight() - v);
				newRegions.add(r4);

			} else {
				// just split horizontally
				Rectangle r1 = new Rectangle(region.getX(), region.getY(), h - region.getX(), region.getHeight());
				Rectangle r2 = new Rectangle(h, region.getY(), region.getX() + region.getWidth() - h,
						region.getHeight());
				newRegions.add(r1);
				newRegions.add(r2);
			}
		} else if (vertical != null) {
			// otherwise split vertically
			Rectangle r1 = new Rectangle(region.getX(), region.getY(), region.getWidth(), v - region.getY());
			Rectangle r2 = new Rectangle(region.getX(), v, region.getWidth(), region.getY() + region.getHeight() - v);
			newRegions.add(r1);
			newRegions.add(r2);
		}

		// add graphically
		for (int i = 0; i < newRegions.size(); i++) {
			newRegions.get(i).setStrokeType(StrokeType.INSIDE);
			newRegions.get(i).setStrokeWidth(4);
			newRegions.get(i).setStroke(Color.BLACK);
			newRegions.get(i).setFill(Color.TRANSPARENT);
			_pane.getChildren().add(newRegions.get(i));
			_regions.add(newRegions.get(i));
		}
		return newRegions;
	}

	/*
	 * returns a number between lower and upper bounds. falls within 33% and 67% of
	 * the length
	 */
	private double locateSplit(double lower, double upper) {
		return lower + (upper - lower) * .33 + (upper - lower) * .33 * Math.random();
	}

	private class TimeHandler implements EventHandler<ActionEvent> {

		@Override
		public void handle(ActionEvent e) {

			if (_border == null) {
				Grid.this.installBorder();
			} else if (_border.getHeight() != _po.getRootHeight()) {
				Grid.this.reset();
				_pane.getChildren().remove(_border);
				Grid.this.installBorder();
			}
			if (!_triggered && _currMode == GridEnums.GO && _border != null) {
				_triggered = true;
				Grid.this.generateGrid(_border);
				_reset = false;
			}
			e.consume();
		}
	}

	private class MouseHandler implements EventHandler<MouseEvent> {

		@Override
		public void handle(MouseEvent e) {
			if (MouseEvent.MOUSE_PRESSED == e.getEventType()) {
				_selectedRegion = Grid.this.getSelectedRegion(e);
				if (_selectedRegion != null) {
					if (_selectedRegion.getFill() == _currColor) {
						_selectedRegion.setFill(Color.TRANSPARENT);
					} else {
						_selectedRegion.setFill(_currColor);
					}
				}
			}

			e.consume();
		}
	}

	private Rectangle getSelectedRegion(MouseEvent e) {
		for (int i = _regions.size() - 1; i > 0; i--) {
			if (_regions.get(i).contains(e.getX(), e.getY())) {
				return _regions.get(i);
			}
		}
		return null;
	}

	public void setColor(Color color) {
		_currColor = color;
	}

	public HelpBox getHB() {
		return _hb;
	}

	public boolean isClear() {
		return _reset;
	}
}
