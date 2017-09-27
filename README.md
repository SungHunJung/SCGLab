SCGLab - FlexAdapter
====================
* Multiple viewholder [sample](/sample/src/main/java/com/scglab/common/MultipleTypeSampleActivity.java)
* Child views click [sample](/sample/src/main/java/com/scglab/common/ItemClickSampleActivity.java)
* Select mode [sample](/sample/src/main/java/com/scglab/common/SelectModeSampleActivity.java)
* Filter [sample](/sample/src/main/java/com/scglab/common/FilterSampleActivity.java)

sample
---------------------
![](/demo/listadapter_base.gif) ![](/demo/listadapter_multiple.gif) ![](/demo/listadapter_click.gif) ![](/demo/listadapter_select.gif) ![](/demo/listadapter_search.gif) 

how to use
---------------------
### 1. item (LabelItem.java)
This is your model class. You only need to add `@FlexAdapter.Item` to your model class. So there is no constraint on the supper class.
```java
@FlexAdapter.Item
public class LabelItem {
  //anything...
}
```
### 2. layout (renderer_label.xml)
```xml
<TextView
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:id="@+id/txtLabel"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"/>
```
### 3. renderer (LabelRenderer.java)
This is ViewHolder. Make sure super class and onBind methods.
```java
public class LabelRenderer extends ItemRenderer<LabelItem> {
  //If the resource name is the same, the view is automatically assigned
  private TextView txtLabel;

  public LabelRenderer(View view) {
    super(view);
  }

  @Override
  protected void onBind(final LabelItem item) {
    txtLabel.setText(item.getLabel());
  }
}
```
### 4. activity side
```java
//rendererFactory
RendererFactory rendererFactory = new RendererFactory();
rendererFactory.put(LabelRenderer.class, R.layout.renderer_label);

//adapter
FlexAdapter flexAdapter = new FlexAdapter(rendererFactory);

//recyclerView
RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
recyclerView.setLayoutManager(new LinearLayoutManager(this));
recyclerView.setAdapter(flexAdapter);

//add items
for (int index = 0; index < 20; index++) {
  flexAdapter.addItem(new LabelItem(String.valueOf(index)));
}
```
download
---------------------
```gradle
dependencies {
    compile 'com.scglab.common:list-adapter:1.1.1'
}
```
history
---------------------
| version | compileSdkVersion | minSdkVersion | date |
| ------ | ------ | ------ | ------ |
| 1.1.1 | 23 | 15 | 25 Sep 2017 |
| 1.1.0 | 23 | 15 | 22 Sep 2017 |
| 1.0.8-beta | 23 | - | 07 Sep 2017 |
| 1.0.7-beta | 23 | - | 08 Sep 2017 |

SCGLab - widget
========
download
---------------------
```gradle
dependencies {
  compile 'com.scglab.common:util:1.0.8-beta'
}
```
history
---------------------
| version | compileSdkVersion | minSdkVersion | date |
| ------ | ------ | ------ | ------ |
| 1.0.8-beta | 23 | - | 07 Sep 2017 |
| 1.0.7-beta | 23 | - | 08 Sep 2017 |

SCGLab - util
========
download
---------------------
```gradle
dependencies {
  compile 'com.scglab.common:util:1.0.8-beta'
}
```
history
---------------------
| version | compileSdkVersion | minSdkVersion | date |
| ------ | ------ | ------ | ------ |
| 1.0.8-beta | 23 | - | 07 Sep 2017 |
| 1.0.7-beta | 23 | - | 08 Sep 2017 |