/*
Copyright (c) 2007-2008 the OTHER media Limited
Licensed under the BSD license, http://ojay.othermedia.org/license.html
Version: 0.4.1
Build:   min
*/

Ojay.Mouse=new JS.Singleton('Ojay.Mouse',{include:JS.Observable,initialize:function(){this.position={left:null,top:null}},updatePosition:function(b,a){var c=YAHOO.util.Event.getXY(a);this.position={left:c[0],top:c[1]};this.notifyObservers(this.position)},on:function(g,d,e,f){if(!/^(?:entering|leaving)$/.test(g))throw new TypeError('Movement is not recognised');var h=(d instanceof Ojay.Region);var i=h?d:null;var d=h?null:Ojay(d);var j=false;this.addObserver(function(b){var a=i||d.getRegion();var c=this.isInside(a);if(g=='entering'&&!j&&c)e.call(f||null,this.position);if(g=='leaving'&&j&&!c)e.call(f||null,this.position);j=c},this)},isInside:function(b){b=Ojay.Region.convert(b);if(!b)return undefined;var a=this.position;return a.left>=b.left&&a.left<=b.right&&a.top>=b.top&&a.top<=b.bottom}});Ojay(document).on('mousemove',Ojay.Mouse.method('updatePosition'));Ojay.DomCollection.include({on:Ojay.DomCollection.prototype.on.wrap(function(){var c=Array.from(arguments),g=c.shift();var d=c[0],e=c[1],f=c[2];if(!/^mouse(enter|leave)$/.test(d))return g(d,e,f);var h=d.match(/^mouse(enter|leave)$/)[1].replace(/e?$/,'ing');var i=new JS.MethodChain();if(e&&typeof e!='function')f=e;this.forEach(function(a){Ojay.Mouse.on(h,a,function(b){if(typeof e=='function')e.call(f||null,a,b);i.fire(f||a)})});return i})});