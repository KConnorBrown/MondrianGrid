function setup() {
  var cnv = createCanvas(windowWidth, windowHeight);
  cnv.style('position', 'absolute');
  hbox = new HelpBox();
}

function windowResized() {
  resizeCanvas(windowWidth, windowHeight);
}

function Rectangle(_x, _y, width, height) {
    this.drawn = true;
    this.x = _x;
    this.y = _y;
    this.width = width;
    this.height = height;
    this.area = this.width*this.height;
    this.color = color(255,255,255,255);
    this.min_x = this.x;
    this.max_x = this.x+this.width;
    this.min_y = this.y;
    this.max_y = this.y+this.height;
}

Rectangle.prototype.draw = function() {
    push();
    fill(this.color);
    strokeWeight(3);
    stroke(0);
    rect(this.x, this.y, this.width, this.height);
    pop();
}

function HelpBox() {
    this.helping = true;
    this.width = 6/7*windowWidth;
    this.height = 6/7*windowHeight;
    this.x = windowWidth/2-this.width/2;
    this.y = windowHeight/2 - this.height/2;
    this.title = createElement('h2', '<h2>Welcome to MondrianGrid!</h2>');
    this.helpTxt = createElement('ul', '<li>type r, b, and y to toggle the fill color (try clicking in boxes!)</li><li>use shift+r to reset the canvas/grid</li><li>use shift+s to save a .png image of your work to share!</li><li>type h to open and close this help box</li>');
    this.title.style('font-size', '50px');
    this.title.style('font-family', "'Roboto', sans-serif");
    this.helpTxt.style('font-size', '36px');
    this.helpTxt.style('font-family', "'Roboto', sans-serif");
    this.title.style('position', 'absolute');
    this.title.style('left', str(1/7*this.width)+'px');
    this.helpTxt.style('left', str(1/7*this.width)+'px');
    this.helpTxt.style('top', str(1/3*this.height)+'px');
    this.helpTxt.style('position', 'absolute');
    this.helpTxt.style('line-height', '75px');
}

HelpBox.prototype.draw = function() {
    push();
    fill(color(230, 241, 245, 240));
    strokeWeight(5);
    stroke(color(230, 0, 0, 240));
    rect(this.x, this.y, this.width, this.height);
    pop();
}


function locateSplit(lower, upper) {
    return lower + (upper-lower)*0.33 + (upper - lower)*0.33*Math.random();
}

/*
I define splitting vertically as choosing a point along the x axis to create two
new rectangles of the original height but narrower.

I define splitting horizontally as choosing a point along the y axis to
horizontally cut a rectangle in half. This preserves width, but makes each
resulting rectangle shorter
*/
function divide(horizontal, vertical, r) {
    newRegions = [];
    var x = locateSplit(r.x, r.x+r.width);
    var y = locateSplit(r.y, r.y+r.height);
    if (horizontal) {
        if (vertical){
            //split horizontally and vertically
            //r1 has x coord r.x, y coord r.y
            //r1 has width x - r.x
            //r1 has height y - r.y
            newRegions.push(new Rectangle(r.x, r.y, x-r.x, y-r.y));

            //r2 has x coord x, y coord r.y
            //r2 has width r.x+r.width-x
            //r2 has height y - r.y
            newRegions.push(new Rectangle(x, r.y, r.x+r.width-x, y-r.y));

            //r3 has x coord r.x, y coord y
            //r3 has width x - r.x
            //r3 has height r.y+r.height - y
            newRegions.push(new Rectangle(r.x, y, x-r.x, r.y+r.height-y));

            //r4 has x coord x, y coord y
            //r4 has width r.x + r.width-x
            //r4 has height r.y+r.height-y
            newRegions.push(new Rectangle(x, y, r.x+r.width-x, r.y+r.height-y));

        //just split horizontally
        } else {
            newRegions.push(new Rectangle(r.x, r.y, r.width, y-r.y));
            newRegions.push(new Rectangle(r.x, y, r.width, r.y+r.height-y));
        }

    //just split vertically
    } else {
        newRegions.push(new Rectangle(r.x, r.y, x-r.x, r.height));
        newRegions.push(new Rectangle(x, r.y, r.x+r.width-x, r.height));
    }
    return newRegions;
}

var rects = [];
var z = .35 + Math.random();
var q = 15 + Math.random()*140;
function generateGrid(r) {
    var newRegions;
    var horizontal, vertical = false;
    //choose two random pivots to determine if region is large enough to split h or v
    var vRand = q+ Math.random() * Math.abs(r.width*1.5 - q);
    var hRand = q+ Math.random() * Math.abs(r.height*1.5 - q);
    //if region is wide and tall enough for both splits
    vertical = r.width > z*windowWidth/2;
    horizontal = r.height > z*windowHeight/2;
    if (!vertical) {
        vertical = r.width > vRand;
    }
    if (!horizontal){
        horizontal = r.height > hRand;
    }
    if (vertical || horizontal) {
         r.drawn = false;
         newRegions = this.divide(horizontal, vertical, r);
         newRegions.forEach(region => generateGrid(region));
         newRegions.forEach(region => rects.push(region));
    }
    return newRegions;
}

function mousePressed(){
    var x = mouseX;
    var y = mouseY;
    var i;
    //find rectangle that contains the point
    for (i=0; i < drawn_rects.length; i ++){
        if (drawn_rects[i].min_x < x && drawn_rects[i].max_x > x
        && drawn_rects[i].min_y < y && drawn_rects[i].max_y > y){
            if (currColor != null) {
                if (currColor == drawn_rects[i].color) {
                    drawn_rects[i].color = color(255,255,255,255);
                } else {
                    drawn_rects[i].color = currColor;
                }
            }
        }
    }
}


var reset = true;
var drawn_rects = [];
function draw() {
    if (reset) {
        reset = false;
        firstRect = new Rectangle(0,0, windowWidth, windowHeight);
        rects.push(firstRect);
        this.generateGrid(firstRect);
        //initialze first Rectangle
        //call generateGrid on first rectangle
        for (i = 0; i < rects.length; i ++) {
            if (rects[i].drawn) {
                drawn_rects.push(rects[i]);
            }
        }
    } else {
        var i;
        for (i = 0; i < drawn_rects.length; i ++) {
            drawn_rects[i].draw();
        }
    }
    if (hbox.helping) {
        hbox.draw();
    }
}

var currColor = null;
function keyPressed(){
    if (key  == 'b'){
        currColor = color('#0000b2');
    } else if (key == 'y') {
        currColor = color('#FFFF32');
    } else if (key == 'r') {
        currColor = color('#FF1919');
    } else if (key == 'S') {
        if (hbox.helping) {
            hbox.helping = false;
            hbox.title.style('visibility', 'hidden');
            hbox.helpTxt.style('visibility', 'hidden');
        }
        redraw();
        saveCanvas();
    } else if (key == 'R') {
        reset = true;
        drawn_rects = [];
        rects = [];
        currColor = null;
        z = .25 + Math.random();
        q = 15 + Math.random()*140;
    } else if (key == 'h') {
        if (hbox.helping) {
            hbox.helping = false;
            hbox.title.style('visibility', 'hidden');
            hbox.helpTxt.style('visibility', 'hidden');
        } else {
            hbox.helping = true;
            hbox.title.style('visibility', 'visible');
            hbox.helpTxt.style('visibility', 'visible');
        }
    }
}
