# MyFit
First Time Creation Of CodeCamp2018 GitHub Project
Recognignition Of Activity

To retrieve maps fragment in DetailActivity:
```
MapFragment mapFragment = (MapFragment) getFragmentManager()
    .findFragmentById(R.id.map);

//Set callback on the fragment
mapFragment.getMapAsync(this);
```
