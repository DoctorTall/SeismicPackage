//////// DO NOT EDIT THIS FILE - it is machine generated ////////

#include "CpseisTrot.hh"

//------------------ fortran spelling adjustments --------------------//
//------------------ fortran spelling adjustments --------------------//
//------------------ fortran spelling adjustments --------------------//

#if NEED_UNDERSCORE
#define trot_wrap_create             trot_wrap_create_
#define trot_wrap_delete             trot_wrap_delete_
#define trot_wrap_update             trot_wrap_update_
#define trot_wrap_wrapup             trot_wrap_wrapup_
#define trot_wrap_oneset             trot_wrap_oneset_
#define trot_wrap_twosets            trot_wrap_twosets_
#elif NEED_CAPITALS
#define trot_wrap_create             TROT_WRAP_CREATE
#define trot_wrap_delete             TROT_WRAP_DELETE
#define trot_wrap_update             TROT_WRAP_UPDATE
#define trot_wrap_wrapup             TROT_WRAP_WRAPUP
#define trot_wrap_oneset             TROT_WRAP_ONESET
#define trot_wrap_twosets            TROT_WRAP_TWOSETS
#endif

//----------------------- fortran prototypes -------------------------//
//----------------------- fortran prototypes -------------------------//
//----------------------- fortran prototypes -------------------------//

extern "C"
    {
    CpseisBase::ModuleCreate   trot_wrap_create;
    CpseisBase::ModuleDestroy  trot_wrap_delete;
    CpseisBase::ModuleUpdate   trot_wrap_update;
    CpseisBase::ModuleWrapup   trot_wrap_wrapup;
    CpseisBase::ModuleOneset   trot_wrap_oneset;
    CpseisBase::ModuleTwosets  trot_wrap_twosets;
    }

//------------------------ constructor -------------------------------//
//------------------------ constructor -------------------------------//
//------------------------ constructor -------------------------------//

CpseisTrot::CpseisTrot() : CpseisBase ("TROT",
                                       trot_wrap_create,
                                       trot_wrap_delete,
                                       trot_wrap_update,
                                       trot_wrap_wrapup,
                                       trot_wrap_oneset,
                                       trot_wrap_twosets) {}

//------------------------------ end ---------------------------------//
//------------------------------ end ---------------------------------//
//------------------------------ end ---------------------------------//