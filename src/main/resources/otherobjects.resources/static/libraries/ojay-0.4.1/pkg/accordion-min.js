/*
Copyright (c) 2007-2008 the OTHER media Limited
Licensed under the BSD license, http://ojay.othermedia.org/license.html
Version: 0.4.1
Build:   min
*/

Ojay.Accordion=new JS.Class('Ojay.Accordion',{include:Ojay.Observable,extend:{DIRECTIONS:{horizontal:'HorizontalSection',vertical:'VerticalSection'}},initialize:function(a,b,c,d){this._8=d||{};this._9=a;this._2=b;this._a=c},setup:function(){var c=this.klass[this.klass.DIRECTIONS[this._9]];this._2=Ojay(this._2).map(function(a,b){var a=new c(this,b,a,this._a,this._8);a.on('expand')._(this).notifyObservers('sectionexpand',b,a);a.on('collapse')._(this).notifyObservers('sectioncollapse',b,a);return a},this);var d=this.getInitialState();this._2[d.section].expand(false);return this},getInitialState:function(){return{section:0}},changeState:function(a){this._2[a.section].expand();return this},_b:function(a,b){if(this._5)this._5.collapse(b);this._5=a},getSections:function(){return this._2.slice()},expand:function(a,b){var c=this._2[a];if(c)c.expand(b);return this},collapse:function(a,b){var c=this._2[a];if(c)c.collapse(b);return this}});Ojay.Accordion.extend({Section:new JS.Class('Ojay.Accordion.Section',{include:Ojay.Observable,extend:{SECTION_CLASS:'accordion-section',COLLAPSER_CLASS:'accordion-collapsible',DEFAULT_EVENT:'click',DEFAULT_DURATION:0.4,DEFAULT_EASING:'easeBoth'},initialize:function(a,b,c,d,e){this._4=a;this._3=c;var f=c.descendants(d).at(0);this._0=Ojay(Ojay.HTML.div({className:this.klass.COLLAPSER_CLASS}));f.insert(this._0,'before');this._0.insert(f);this._3.insert(this._0,'after');this._0.setStyle({position:'relative',zoom:1});e=e||{};this._6=e.duration||this.klass.DEFAULT_DURATION;this._7=e.easing||this.klass.DEFAULT_EASING;this._3.addClass(this.klass.SECTION_CLASS);this._3.on(e.event||this.klass.DEFAULT_EVENT)._(this._4).changeState({section:b});if(e.collapseOnClick)this._3.on('click',function(){if(this._1)this.collapse()},this);this._1=true;this.collapse(false)},getContainer:function(){return this._3},getCollapser:function(){return this._0},collapse:function(b){if(!this._1)return this;this._0.setStyle({overflow:'hidden'});this._3.removeClass('expanded').addClass('collapsed');var c={};c[this.param]=(b===false)?0:{to:0};var d=this._4;if(b!==false)this.notifyObservers('collapse');if(b===false){this._0.setStyle(c).setStyle({overflow:'hidden'});this._1=false;return this}else{return this._0.animate(c,this._6,{easing:this._7}).setStyle({overflow:'hidden'})._(function(a){a._1=false},this)._(this)}},expand:function(b){if(this._1)return this;this._4._b(this,b);this._0.setStyle({overflow:'hidden'});this._3.addClass('expanded').removeClass('collapsed');var c=this.getSize(),d={},e={overflow:''};d[this.param]=(b===false)?'':{to:c};e[this.param]='';var f=this._4;if(b!==false)this.notifyObservers('expand');if(b===false){this._0.setStyle(d).setStyle({overflow:''});this._1=true;return this}else{return this._0.animate(d,this._6,{easing:this._7}).setStyle(e)._(function(a){a._1=true},this)._(this)}}})});Ojay.Accordion.extend({HorizontalSection:new JS.Class('Ojay.Accordion.HorizontalSection',Ojay.Accordion.Section,{param:'width',getSize:function(){var d=this._4.getSections();var e=d.map(function(a){var b=a._0,c=b.getRegion().getWidth();b.setStyle({width:a==this?'':0});return c},this);var f=this._0.getRegion().getWidth();d.forEach(function(a,b){a._0.setStyle({width:e[b]+'px'})});return f}}),VerticalSection:new JS.Class('Ojay.Accordion.VerticalSection',Ojay.Accordion.Section,{param:'height',getSize:function(){if(!this._1)this._0.setStyle({height:''});var a=this._0.getRegion().getHeight();if(!this._1)this._0.setStyle({height:0});return a}})});