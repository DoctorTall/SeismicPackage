!!------------------------ dmap3d_wrap.f90 --------------------------!!
!!------------------------ dmap3d_wrap.f90 --------------------------!!
!!------------------------ dmap3d_wrap.f90 --------------------------!!

!!!!!!!!!! DO NOT EDIT THIS FILE - it is machine generated !!!!!!!!!!

!!------------------------------ module -----------------------------------!!
!!------------------------------ module -----------------------------------!!
!!------------------------------ module -----------------------------------!!

      module dmap3d_wrap_module
      use dmap3d_module
      implicit none
      public

      type :: dmap3d_wrap_struct
        type(dmap3d_struct),pointer :: obj
      end type dmap3d_wrap_struct

      end module dmap3d_wrap_module

!!---------------------------- create ------------------------------------!!
!!---------------------------- create ------------------------------------!!
!!---------------------------- create ------------------------------------!!

      subroutine dmap3d_wrap_create (fpoint)  
      use dmap3d_wrap_module
      implicit none
      type(dmap3d_wrap_struct),intent(out)   :: fpoint
      type(dmap3d_struct)     ,pointer       :: obj           ! local

      nullify (obj)               ! needed for intel compiler.
      call dmap3d_create (obj)
      fpoint%obj => obj

      end subroutine dmap3d_wrap_create

!!---------------------------- delete ------------------------------------!!
!!---------------------------- delete ------------------------------------!!
!!---------------------------- delete ------------------------------------!!

      subroutine dmap3d_wrap_delete (fpoint)   
      use dmap3d_wrap_module
      implicit none
      type(dmap3d_wrap_struct),intent(inout) :: fpoint
      type(dmap3d_struct)     ,pointer       :: obj           ! local

      obj => fpoint%obj
      if (.not. associated(obj)) return
      call dmap3d_delete (obj)
      fpoint%obj => obj

      end subroutine dmap3d_wrap_delete

!!---------------------------- update ------------------------------------!!
!!---------------------------- update ------------------------------------!!
!!---------------------------- update ------------------------------------!!

      subroutine dmap3d_wrap_update (fpoint)     
      use dmap3d_wrap_module
      implicit none
      type(dmap3d_wrap_struct),intent(inout) :: fpoint
      type(dmap3d_struct)     ,pointer       :: obj           ! local

      obj => fpoint%obj
      if (.not. associated(obj)) return
      call dmap3d_update (obj)

      end subroutine dmap3d_wrap_update

!!---------------------------- wrapup ------------------------------------!!
!!---------------------------- wrapup ------------------------------------!!
!!---------------------------- wrapup ------------------------------------!!

      subroutine dmap3d_wrap_wrapup (fpoint)      
      use dmap3d_wrap_module
      implicit none
      type(dmap3d_wrap_struct),intent(inout) :: fpoint
      type(dmap3d_struct)     ,pointer       :: obj           ! local

      obj => fpoint%obj
      if (.not. associated(obj)) return
      call dmap3d_wrapup (obj)

      end subroutine dmap3d_wrap_wrapup

!!---------------------------- oneset ------------------------------------!!
!!---------------------------- oneset ------------------------------------!!
!!---------------------------- oneset ------------------------------------!!

      subroutine dmap3d_wrap_oneset (fpoint,ntr,  &
                                   hd,tr,lenhd,lentr,num)
      use dmap3d_wrap_module
      implicit none
      type(dmap3d_wrap_struct),intent(inout) :: fpoint
      integer               ,intent(in)    :: lenhd,lentr,num  ! argument
      integer               ,intent(inout) :: ntr              ! argument
      double precision      ,intent(inout) :: hd(lenhd,num)    ! argument
      real                  ,intent(inout) :: tr(lentr,num)    ! argument
      type(dmap3d_struct)     ,pointer       :: obj              ! local

      obj => fpoint%obj
      if (.not. associated(obj)) return
      call dmap3d (obj,ntr,hd,tr)                ! might be commented out.

      end subroutine dmap3d_wrap_oneset

!!---------------------------- twosets ------------------------------------!!
!!---------------------------- twosets ------------------------------------!!
!!---------------------------- twosets ------------------------------------!!

      subroutine dmap3d_wrap_twosets (fpoint,ntr,                 &
                                    hd1,tr1,lenhd1,lentr1,num1, &
                                    hd2,tr2,lenhd2,lentr2,num2)
      use dmap3d_wrap_module
      implicit none
      type(dmap3d_wrap_struct),intent(inout) :: fpoint
      integer               ,intent(in)    :: lenhd1,lentr1,num1 ! argument
      integer               ,intent(in)    :: lenhd2,lentr2,num2 ! argument
      integer               ,intent(inout) :: ntr                ! argument
      double precision      ,intent(inout) :: hd1(lenhd1,num1)   ! argument
      real                  ,intent(inout) :: tr1(lentr1,num1)   ! argument
      double precision      ,intent(inout) :: hd2(lenhd2,num2)   ! argument
      real                  ,intent(inout) :: tr2(lentr2,num2)   ! argument
      type(dmap3d_struct)     ,pointer       :: obj                ! local

      obj => fpoint%obj
      if (.not. associated(obj)) return
!!!!  call dmap3d (obj,ntr,hd1,tr1,hd2,tr2)      ! might be commented out.

      end subroutine dmap3d_wrap_twosets

!!----------------------------- end ---------------------------------------!!
!!----------------------------- end ---------------------------------------!!
!!----------------------------- end ---------------------------------------!!
