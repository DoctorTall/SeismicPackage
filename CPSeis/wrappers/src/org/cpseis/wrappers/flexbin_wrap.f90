!!------------------------ flexbin_wrap.f90 --------------------------!!
!!------------------------ flexbin_wrap.f90 --------------------------!!
!!------------------------ flexbin_wrap.f90 --------------------------!!

!!!!!!!!!! DO NOT EDIT THIS FILE - it is machine generated !!!!!!!!!!

!!------------------------------ module -----------------------------------!!
!!------------------------------ module -----------------------------------!!
!!------------------------------ module -----------------------------------!!

      module flexbin_wrap_module
      use flexbin_module
      implicit none
      public

      type :: flexbin_wrap_struct
        type(flexbin_struct),pointer :: obj
      end type flexbin_wrap_struct

      end module flexbin_wrap_module

!!---------------------------- create ------------------------------------!!
!!---------------------------- create ------------------------------------!!
!!---------------------------- create ------------------------------------!!

      subroutine flexbin_wrap_create (fpoint)  
      use flexbin_wrap_module
      implicit none
      type(flexbin_wrap_struct),intent(out)   :: fpoint
      type(flexbin_struct)     ,pointer       :: obj           ! local

      nullify (obj)               ! needed for intel compiler.
      call flexbin_create (obj)
      fpoint%obj => obj

      end subroutine flexbin_wrap_create

!!---------------------------- delete ------------------------------------!!
!!---------------------------- delete ------------------------------------!!
!!---------------------------- delete ------------------------------------!!

      subroutine flexbin_wrap_delete (fpoint)   
      use flexbin_wrap_module
      implicit none
      type(flexbin_wrap_struct),intent(inout) :: fpoint
      type(flexbin_struct)     ,pointer       :: obj           ! local

      obj => fpoint%obj
      if (.not. associated(obj)) return
      call flexbin_delete (obj)
      fpoint%obj => obj

      end subroutine flexbin_wrap_delete

!!---------------------------- update ------------------------------------!!
!!---------------------------- update ------------------------------------!!
!!---------------------------- update ------------------------------------!!

      subroutine flexbin_wrap_update (fpoint)     
      use flexbin_wrap_module
      implicit none
      type(flexbin_wrap_struct),intent(inout) :: fpoint
      type(flexbin_struct)     ,pointer       :: obj           ! local

      obj => fpoint%obj
      if (.not. associated(obj)) return
      call flexbin_update (obj)

      end subroutine flexbin_wrap_update

!!---------------------------- wrapup ------------------------------------!!
!!---------------------------- wrapup ------------------------------------!!
!!---------------------------- wrapup ------------------------------------!!

      subroutine flexbin_wrap_wrapup (fpoint)      
      use flexbin_wrap_module
      implicit none
      type(flexbin_wrap_struct),intent(inout) :: fpoint
      type(flexbin_struct)     ,pointer       :: obj           ! local

      obj => fpoint%obj
      if (.not. associated(obj)) return
      call flexbin_wrapup (obj)

      end subroutine flexbin_wrap_wrapup

!!---------------------------- oneset ------------------------------------!!
!!---------------------------- oneset ------------------------------------!!
!!---------------------------- oneset ------------------------------------!!

      subroutine flexbin_wrap_oneset (fpoint,ntr,  &
                                   hd,tr,lenhd,lentr,num)
      use flexbin_wrap_module
      implicit none
      type(flexbin_wrap_struct),intent(inout) :: fpoint
      integer               ,intent(in)    :: lenhd,lentr,num  ! argument
      integer               ,intent(inout) :: ntr              ! argument
      double precision      ,intent(inout) :: hd(lenhd,num)    ! argument
      real                  ,intent(inout) :: tr(lentr,num)    ! argument
      type(flexbin_struct)     ,pointer       :: obj              ! local

      obj => fpoint%obj
      if (.not. associated(obj)) return
      call flexbin (obj,ntr,hd,tr)                ! might be commented out.

      end subroutine flexbin_wrap_oneset

!!---------------------------- twosets ------------------------------------!!
!!---------------------------- twosets ------------------------------------!!
!!---------------------------- twosets ------------------------------------!!

      subroutine flexbin_wrap_twosets (fpoint,ntr,                 &
                                    hd1,tr1,lenhd1,lentr1,num1, &
                                    hd2,tr2,lenhd2,lentr2,num2)
      use flexbin_wrap_module
      implicit none
      type(flexbin_wrap_struct),intent(inout) :: fpoint
      integer               ,intent(in)    :: lenhd1,lentr1,num1 ! argument
      integer               ,intent(in)    :: lenhd2,lentr2,num2 ! argument
      integer               ,intent(inout) :: ntr                ! argument
      double precision      ,intent(inout) :: hd1(lenhd1,num1)   ! argument
      real                  ,intent(inout) :: tr1(lentr1,num1)   ! argument
      double precision      ,intent(inout) :: hd2(lenhd2,num2)   ! argument
      real                  ,intent(inout) :: tr2(lentr2,num2)   ! argument
      type(flexbin_struct)     ,pointer       :: obj                ! local

      obj => fpoint%obj
      if (.not. associated(obj)) return
!!!!  call flexbin (obj,ntr,hd1,tr1,hd2,tr2)      ! might be commented out.

      end subroutine flexbin_wrap_twosets

!!----------------------------- end ---------------------------------------!!
!!----------------------------- end ---------------------------------------!!
!!----------------------------- end ---------------------------------------!!
