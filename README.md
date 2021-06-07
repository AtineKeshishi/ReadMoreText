# ReadMoreTextView
By using this custom textView, when TextView's text is too long, we can collapse the text.

# How to use
To use the ReadMoreText on your app, add the following code to your layout:

```
<com.akeshishi.readmoretext.ReadMoreText
    android:id="@+id/textView"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"/>
```
You can customize ReadMoreText with:

* app:expandingText: Text that appears when the view is collapsed.
* app:collapsingText: Text that appears when the view is expanded.
* app:readMoreMaxLines: Max lines to determine when the clickable text is displayed.
* app:readMoreTextColor: Text color of trim clickable text.

# Screenshots
<img src="https://github.com/AtineKeshishi/ReadMoreText/blob/master/screenshots/screenshot1.png" width="200" height="400" /> |
<img src="https://github.com/AtineKeshishi/ReadMoreText/blob/master/screenshots/screenshot2.png" width="200" height="400" />
