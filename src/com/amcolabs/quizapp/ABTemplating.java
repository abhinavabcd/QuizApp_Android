package com.amcolabs.quizapp;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map.Entry;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;
/*
 *  Render json templating model to help create clean ui's without planning ,
 *  just know what data you need to display, and how much space they need , render json will auto compute the layout
 *  You can also write your templates in easy on-the-go fashion and not really giving a serious a thought. 
 *  No xml here , so its a little pain on creating the elements yourself and manipulation, but hey , it's programming after all.
 *  See the templates at this file end created , they should be self explanatory
 */

public class ABTemplating{
	public final static int NONE = -5;
	public enum ViewType{
		SCROLL_VIEW,
		NORMAL, HORIZONTAL_SCROLL_VIEW;
	}
	public final static boolean IS_HORIZONTAL_SCROLL = true;
	public static class ABView  extends LinearLayout{
		private HashMap<String, ABView> cells = new HashMap<String, ABView>();
		private String name=null;
		
		ViewType viewType = ViewType.NORMAL;
		private Object tag2;
		
		public ABView(Context context, String id) {
			super(context, null);
			name = id;
			cells.put(name, this);
		}
		

		public ABView(Context context, int styleResource ) {
			super(context,null,styleResource);
		}

		public ABView(Context context, int styleResource , String id ) {
			super(context,null,styleResource);
			name = id;
			cells.put(name, this);
		}

		public ABView(Context context, ABView ...views) {
			super(context);
			registerInnerViews(views);
		}
	
		

		public void registerInnerView(ABView view){
			cells.putAll(view.getAllCells());
		}
		
		public void registerInnerViews(ABView[] views){
			for(ABView view : views){
				cells.putAll(view.getAllCells());
			}
		}
		


		public HashMap<String, ABView> getAllCells() {
			return cells;
		}
		
		public ABView getCell(String cellName) {
			return cells.get(cellName);
		}
		public ABView setCell(String cellName, ABView view) {
			return cells.put(cellName, view);
		}

		public ABView gty(int gravity) {
			this.setGravity(gravity);
			return this;
		}
		public ABView wd(int width) {
			if(this.getLayoutParams()==null)
				this.setLayoutParams(new LayoutParams(width, getHeight()==0?LayoutParams.WRAP_CONTENT:getHeight()));
			else
				((LayoutParams)this.getLayoutParams()).width = width;
			return this;
		}
		public ABView ht(int height) {
			if(this.getLayoutParams()==null)
				this.setLayoutParams(new LayoutParams(getWidth()==0?LayoutParams.WRAP_CONTENT:getWidth(),height));
			else
				((LayoutParams)this.getLayoutParams()).width = height;
			return this;
		}
		
		public ABView wgt(float weight) {
			if(this.getLayoutParams()==null || (getHeight()==0 && getWidth()==0))
				this.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		
			((LayoutParams)this.getLayoutParams()).weight = weight;
			return this;
		}


		public ABView addLabel(String string) {
			TextView label = new TextView(getContext(),null);
			label.setText(string);
			this.setPadding(15,0,0,15);
			this.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT));
			this.setGravity(Gravity.CENTER_VERTICAL);
			this.addView(label);
			return this;
		}
		
		@Override
		  protected void removeDetachedView(View child, boolean animate) {
		     super.removeDetachedView(child, false);
		  }	
		
		public ABView asVScrollView(ABView...views){
			ABView scrollChild = new ABView(getContext(),name);
			scrollChild.setOrientation(LinearLayout.VERTICAL);
			scrollChild.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
			ScrollView scrollView = new ScrollView(getContext());
			scrollView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
			scrollView.addView(scrollChild);
			this.addView(scrollView);
			this.viewType = ViewType.SCROLL_VIEW;
			//add all views inside this to scroller
			if(views.length>0){
				for(ABView view : views){
					this.addView(view);
				}
			}
			return this;
		}

		public ABView asHScrollView(ABView...views){
			ABView scrollChild = new ABView(getContext(),name);
			scrollChild.setOrientation(LinearLayout.HORIZONTAL);
			scrollChild.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
			HorizontalScrollView scrollView = new HorizontalScrollView(getContext());
			scrollView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			scrollView.addView(scrollChild);
			this.addView(scrollView);
			this.viewType = ViewType.HORIZONTAL_SCROLL_VIEW;
			//add all views inside this to scroller
			if(views.length>0){
				for(ABView view : views){
					this.addView(view);
				}
			}
			return this;
		}

		
		public ABView addView(ABView child) {
			registerInnerView(child);
			switch(viewType){
				case SCROLL_VIEW:
					((ABView)((ScrollView)this.getChildAt(0)).getChildAt(0)).addView(child);
					break;
				case HORIZONTAL_SCROLL_VIEW:
					((ABView)((HorizontalScrollView)this.getChildAt(0)).getChildAt(0)).addView(child);
					break;
				
				default:
					super.addView(child);
			}
			return this;
		}
		public ABView addView(ABView child, int index) {
			// TODO Auto-generated method stub
			registerInnerView(child);
			switch(viewType){
				case SCROLL_VIEW:
					((ABView)((ScrollView)this.getChildAt(0)).getChildAt(0)).addView(child, index);
					break;
				default:
					super.addView(child, index);
			}
			return this;
		}
		
		public ABView getScrollChildAt(int index) {
			switch (viewType) {
				case SCROLL_VIEW:
					return (ABView) ((ABView)((ScrollView)getChildAt(0)).getChildAt(0)).getChildAt(index);
				case HORIZONTAL_SCROLL_VIEW:
					return (ABView) ((ABView)((HorizontalScrollView)getChildAt(0)).getChildAt(0)).getChildAt(index);
				default:
					break;
			}
			return (ABView) super.getChildAt(index);
		}
		
		public ABView underline(){
			View v = new View(getContext());
			v.setLayoutParams(new LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT , 1));
			v.setBackgroundColor(Color.GRAY);
			this.addView(v);
			return this;
		}


		public void setTag2(Object pendingJob) {
			tag2 = pendingJob;
		}


		public Object getTag2() {
			return tag2;
		}
		
	}
	
	Context ctx = null;
	public ABTemplating(Context ctx){
		this.ctx = ctx;
	}
	public Button createButton(String title, String style , int width , int height){
		return null;
	}
	
	public void createRadio(){
	
	}
	
	public void createDropDown(){
	
	}
	
	public void createRowGrid(){
	
	}

	public ABView h(ABView ...views) {
		ABView ret = new ABView(ctx);		
		return _h(ret, views);
	}
	
	public ABView h(int style, ABView ...views) {
		ABView ret = new ABView(ctx, style);
		return _h(ret, views);
	}

	
	public ABView h(boolean isScroll, ABView ...views) {
		ABView ret = null;
		if(isScroll)
			ret = new ABView(ctx).asHScrollView();		
		else
	         ret = new ABView(ctx);		
		
		return _h(ret, views);
	}

	public ABView v(boolean isScroll, ABView ...views) {
		ABView ret = null;
		if(isScroll)
			ret = new ABView(ctx).asVScrollView();		
		else
	         ret = new ABView(ctx);		
		
		return _v(ret, views);
	}

	
	public ABView v(ABView ...views) {
		ABView ret = new ABView(ctx);		
		return _v(ret, views);
	}
	
	public ABView v(int style, ABView ...views) {
		ABView ret = new ABView(ctx, style);
		return _v(ret, views);
	}
	
	private ABView _h(ABView ret , ABView ...views) {
			
			ret.setOrientation(LinearLayout.HORIZONTAL);
//			ret.registerInnerViews(views); //add view registers it now
			for(ABView view: views){
				ret.addView(view);
			}
			return ret;
	}
	
	private ABView _v(ABView ret ,ABView ...views) {
		ret.setOrientation(LinearLayout.VERTICAL);
//		ret.registerInnerViews(views); //add view registers it now
		for(ABView view: views){
			view.wd(LayoutParams.MATCH_PARENT);
			ret.addView(view);
		}
		ret.wd(LayoutParams.MATCH_PARENT);
		return ret;
	}

	
	public Object loadViewAsObject(ABView view , Class<?> clazz){
		HashMap<String, ABView> cellNames = view.getAllCells();
		try {
			Object object = clazz.newInstance();
			for(Entry<String, ABView> entry: cellNames.entrySet()){
				set(object, entry.getKey() , entry.getValue());
			}
			return object;
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public static boolean set(Object object, String fieldName, Object fieldValue) {
	    Class<?> clazz = object.getClass();
	    while (clazz != null) {
	        try {
	            Field field = clazz.getDeclaredField(fieldName+"Holder");
	            field.setAccessible(true);
	            field.set(object, fieldValue);
	            return true;
	        } catch (NoSuchFieldException e) {
	            clazz = clazz.getSuperclass();
	        } catch (Exception e) {
	            throw new IllegalStateException(e);
	        }
	    }
	    return false;
	}
	
	/*
	 * all app based templates here 
	 */
	
	public ABView getVideoMainTemplate(){
		return v(
				h( new ABView(ctx,"label").addLabel("YURL:"),new ABView(ctx, "url").wgt(1.0f),new ABView(ctx, "load")), 
				new ABView(ctx, "video"),
				h(IS_HORIZONTAL_SCROLL, new ABView(ctx, "AtoB"), new ABView(ctx, "recordButton"),new ABView(ctx, "saveToJobs")),
				new ABView(ctx,"temp2").underline(),
				v(true,
					new ABView(ctx, "label3").addLabel("Current Editing").wd(LayoutParams.MATCH_PARENT).gty(Gravity.CENTER),
					new ABView(ctx, "pendingJob"),
					new ABView(ctx, "jobStatus"),
					new ABView(ctx,"temp").underline(),
					new ABView(ctx, "label2").addLabel("Saved Jobs").wd(LayoutParams.MATCH_PARENT).gty(Gravity.CENTER),
					new ABView(ctx, "savedJobs").asVScrollView(),
					new ABView(ctx, "submitButton"),
					new ABView(ctx, "debug").addLabel("")
				)
		);
	}
	
	public ABView getSavedRecordingTemplate(){
		return v( 
				 h( 
				     new ABView(ctx,"label").addLabel("Title"),
			    	 new ABView(ctx,"titleEdit").wgt(1.0f)
				  ),
				h(	new ABView(ctx,"timeline").wgt(1.0f).gty(Gravity.CENTER),
					new ABView(ctx, "playButton"),
					new ABView(ctx,"deleteButton"),
					new ABView(ctx,"statustext")
				).gty(Gravity.CENTER_VERTICAL).wd(LayoutParams.MATCH_PARENT)
				);
	}
	
}
	