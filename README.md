# Touchless Chef

###### tags: `GitHub`

>[50.001](https://istd.sutd.edu.sg/undergraduate/courses/50001-information-systems-programming) 1D Project 
>
>A POC Recipe Android Application using Google Mediapipe Hands Tracking model for smart touchless scrolling.
>
>Deliverables:
>- Download and install our `.apk` [***HERE***](https://drive.google.com/file/d/1cFvYaZcH2iSLYQ5tJQUPIS5-JyAQbOzd/view?usp=sharing) to try it out
>- [Slides](https://drive.google.com/file/d/1CY5GiMf-hH6lPLB7P39XUdJx_tyvs2Lg/view)
>- [Poster](https://drive.google.com/file/d/1Y4r8ZRK22YiWPUSEJxjkPF3D8pMjsIxF/view?usp=sharing)
>- [Video](https://drive.google.com/file/d/1eMMJQq2ctQO_Nt-fLtD9gfOnbFEruVBF/view?usp=sharing)

## A. Acknowledgement:
- Tran Nguyen Bao Long [@TNBL](https://github.com/TNBL265): Project Lead
    - define software architecture and project scoping
    - documentation
- Li Xueqing [@cnmnrll](https://github.com/cnmnrll): Project Design
    - set basic `Recipe`, `Ingredients` and `Instructions` models in [`model`](https://github.com/TNBL265/TouchlessChef/tree/main/hands/src/main/java/app/touchlessChef/model)
    - documentation
- Melodie Chew En Qi [@melmelchew](https://github.com/melmelchew): Marketing + Frontend
    - `RecyclerView` and `DrawerLayout` for navigation
    - [`adapter`](https://github.com/TNBL265/TouchlessChef/tree/main/hands/src/main/java/app/touchlessChef/adapter/recipe) for `RecyclerView`
- Melvin Lim Boon Han [@melvinlimbh](https://github.com/melvinlimbh): Marketing + Frontend
    - Fragments for different cuisines in [`fragment/cuisine`](https://github.com/TNBL265/TouchlessChef/tree/main/hands/src/main/java/app/touchlessChef/fragment/cuisine)
    - Fragments for creating recipes in [`fragment/recipe`](https://github.com/TNBL265/TouchlessChef/tree/main/hands/src/main/java/app/touchlessChef/fragment/recipe)
    - all resources and layouts in [`res`](https://github.com/TNBL265/TouchlessChef/tree/main/hands/src/main/res)
- Han Jin [@dlsmzhhh](https://github.com/dlsmzhhh): Backend Acitivity
    - basic recipe activities in [`acitivity`](https://github.com/TNBL265/TouchlessChef/tree/main/hands/src/main/java/app/touchlessChef/activity)
- Wang Zhouran [@wzrwzr23](https://github.com/wzrwzr23): Backend Database
    - setup `SQLite` database and corresponding Data Access Objects in [`dao`](https://github.com/TNBL265/TouchlessChef/tree/main/hands/src/main/java/app/touchlessChef/dao) and Adapter in [`adapter`](https://github.com/TNBL265/TouchlessChef/blob/main/hands/src/main/java/app/touchlessChef/adapter/DatabaseAdapter.java)
- Liang Junyi [@LiangJunyi-010](https://github.com/LiangJunyi-010) Backend HandTracking
    - implement HandTracking using Google Mediapipe [**Hands model**](https://google.github.io/mediapipe/solutions/hands.html) for touchless scrolling in [`ViewRecipeActivity`](https://github.com/TNBL265/TouchlessChef/blob/main/hands/src/main/java/app/touchlessChef/activity/recipe/ViewRecipeActivity.java)

## [B. More Technical Details](https://hackmd.io/@TNBL/HylT4u7k5)

