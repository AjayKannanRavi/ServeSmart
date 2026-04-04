import React from 'react';
import { 
  TrendingUp, Users, Zap, Star, Package, 
  ArrowUpRight, Clock, ShieldCheck, 
  CreditCard, Layout, ChevronRight
} from 'lucide-react';

const HotelSaaSDashboard = ({ analytics, restaurant, tables, staff, setActiveTab }) => {
  const getPlanConfig = (type) => {
    switch (type) {
      case 'PREMIUM': return { limit: 1000, label: 'Unlimited', color: 'indigo' };
      case 'CLASSIC': return { limit: 20, label: '20 Tables', color: 'amber' };
      case 'STARTER': return { limit: 5, label: '5 Tables', color: 'blue' };
      default: return { limit: 5, label: '5 Tables', color: 'gray' };
    }
  };

  const planConfig = getPlanConfig(restaurant.planType);
  const tableLimit = planConfig.label;
  const tableUsagePct = (tables.length / planConfig.limit) * 100;

  const stats = [
    { 
      label: 'Total Revenue', 
      value: `₹${(analytics.summary?.totalRevenue || 0).toLocaleString('en-IN')}`, 
      change: '+12.5%', 
      icon: TrendingUp, 
      color: 'amber' 
    },
    { 
      label: 'Active Tables', 
      value: `${tables.length} / ${tableLimit}`, 
      change: 'In Use', 
      icon: Layout, 
      color: 'blue' 
    },
    { 
      label: 'Avg Rating', 
      value: `${(analytics.reviews?.averageRating || 0).toFixed(1)}`, 
      change: `${analytics.reviews?.totalReviews || 0} reviews`, 
      icon: Star, 
      color: 'purple' 
    },
    { 
      label: 'Staff Active', 
      value: staff.length, 
      change: 'Roles sync', 
      icon: Users, 
      color: 'orange' 
    },
  ];

  return (
    <div className="space-y-8 animate-in fade-in duration-500">
      {/* Welcome & Subscription Header */}
      <div className="flex flex-col md:flex-row justify-between items-start md:items-center gap-6 bg-white p-8 rounded-[2.5rem] border border-gray-100 shadow-sm overflow-hidden relative">
        <div className="absolute top-0 right-0 w-64 h-64 bg-amber-500/5 rounded-full -mr-32 -mt-32 blur-3xl"></div>
        <div className="relative z-10">
          <h1 className="text-4xl font-black text-gray-900 tracking-tight mb-2">
            Welcome back, {restaurant.name}
          </h1>
          <div className="flex items-center gap-3">
            <span className={`flex items-center gap-1.5 px-3 py-1 rounded-full text-[10px] font-black tracking-widest bg-${planConfig.color}-100 text-${planConfig.color}-700`}>
              <ShieldCheck size={12} /> {restaurant.planType} PLAN
            </span>
            <span className="text-gray-400 font-bold text-xs">
              {restaurant.planExpiry ? `Expires ${new Date(restaurant.planExpiry).toLocaleDateString()}` : 'Lifetime Access'}
            </span>
          </div>
        </div>
        
        {restaurant.planType !== 'PREMIUM' && (
          <button onClick={() => setActiveTab('hotel')} className="relative z-10 bg-gray-900 text-white px-8 py-4 rounded-2xl font-black hover:bg-black transition shadow-xl shadow-gray-200 flex items-center gap-2 group cursor-pointer">
            <CreditCard size={20} className="group-hover:rotate-12 transition" />
            Upgrade Plan
          </button>
        )}
      </div>

      {/* Grid of Stats */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        {stats.map((stat, i) => (
          <div key={i} className="bg-white p-6 rounded-3xl border border-gray-100 shadow-sm hover:shadow-md transition">
            <div className={`p-4 bg-${stat.color}-50 text-${stat.color}-600 rounded-2xl w-fit mb-4`}>
              <stat.icon size={24} />
            </div>
            <p className="text-[10px] font-black uppercase text-gray-400 tracking-widest mb-1">{stat.label}</p>
            <h3 className="text-2xl font-black text-gray-900 mb-1">{stat.value}</h3>
            <div className="flex items-center gap-1 text-[10px] font-black text-emerald-500">
               <ArrowUpRight size={12} /> {stat.change}
            </div>
          </div>
        ))}
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-12 gap-8">
        {/* Business Health Card */}
        <div className="lg:col-span-8 bg-white p-8 rounded-[2.5rem] border border-gray-100 shadow-sm">
           <div className="flex justify-between items-center mb-8">
             <h3 className="font-black text-xl text-gray-900">Platform Usage</h3>
             <span className="text-[10px] font-black text-gray-400 uppercase tracking-widest">Live Monitoring</span>
           </div>
           
           <div className="space-y-8">
             {/* Table Usage */}
             <div>
               <div className="flex justify-between items-end mb-3">
                 <div>
                   <p className="font-black text-gray-900">Table Capacity</p>
                   <p className="text-xs text-gray-500 font-medium">Manage up to {tableLimit} tables on your current plan.</p>
                 </div>
                 <p className="font-black text-gray-900">{tables.length} / {tableLimit}</p>
               </div>
               <div className="h-4 bg-gray-50 rounded-full overflow-hidden border border-gray-100">
                 <div 
                   className={`h-full transition-all duration-1000 rounded-full ${tableUsagePct > 80 ? 'bg-orange-500' : 'bg-amber-500'}`}
                   style={{ width: `${Math.min(tableUsagePct, 100)}%` }}
                 ></div>
               </div>
                {tableUsagePct >= 80 && (
                  <p className="mt-2 text-[10px] font-black text-orange-600 flex items-center gap-1">
                    <Clock size={12} /> Almost at capacity! Upgrade for more tables.
                  </p>
                )}
             </div>              {/* Peak Hours & Revenue Source */}
              <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
                 <div className="bg-gray-50 p-6 rounded-3xl border border-gray-100">
                   <div className="flex items-center gap-4 mb-4">
                     <div className="w-12 h-12 bg-amber-100 text-amber-600 rounded-2xl flex items-center justify-center">
                       <Clock size={24} />
                     </div>
                     <div>
                       <p className="font-black text-gray-900">Peak Performance</p>
                       <p className="text-[10px] font-bold text-gray-400">HOUR WITH MOST ORDERS</p>
                     </div>
                   </div>
                   <p className="text-sm font-medium text-gray-600 leading-relaxed mb-4">
                     {analytics.peakHours?.length > 0 
                       ? `Your busiest hour is ${analytics.peakHours[0].hour}:00 with ${analytics.peakHours[0].count} orders.` 
                       : "Not enough data to calculate peak hours yet."}
                   </p>
                   <div className="flex gap-1 h-8 items-end">
                      {(analytics.peakHours || []).slice(0, 12).map((h, i) => (
                        <div key={i} className="flex-1 bg-amber-200 rounded-t-sm" style={{ height: `${(h.count / Math.max(...analytics.peakHours.map(x => x.count), 1)) * 100}%` }}></div>
                      ))}
                   </div>
                 </div>

                 <div className="bg-gray-50 p-6 rounded-3xl border border-gray-100">
                   <div className="flex items-center gap-4 mb-4">
                     <div className="w-12 h-12 bg-blue-100 text-blue-600 rounded-2xl flex items-center justify-center">
                       <TrendingUp size={24} />
                     </div>
                     <div>
                       <p className="font-black text-gray-900">Revenue Stream</p>
                       <p className="text-[10px] font-bold text-gray-400">TOP CATEGORY</p>
                     </div>
                   </div>
                   <p className="text-sm font-medium text-gray-600 leading-relaxed mb-4">
                     {analytics.categories?.length > 0 
                       ? `"${analytics.categories[0].category}" is your top performer, contributing ₹${analytics.categories[0].revenue.toLocaleString('en-IN')}.`
                       : "No category data available yet."}
                   </p>
                   <div className="w-full h-2 bg-gray-200 rounded-full overflow-hidden">
                      <div className="h-full bg-blue-500" style={{ width: '70%' }}></div>
                   </div>
                 </div>
              </div>

             {/* Order Accuracy/Success */}
             <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
                <div className="bg-gray-50 p-6 rounded-3xl border border-gray-100">
                  <div className="flex items-center gap-4 mb-4">
                    <div className="w-12 h-12 bg-amber-100 text-amber-600 rounded-2xl flex items-center justify-center">
                      <Package size={24} />
                    </div>
                    <div>
                      <p className="font-black text-gray-900">Inventory Status</p>
                      <p className="text-[10px] font-bold text-gray-400">STOCK HEALTH</p>
                    </div>
                  </div>
                  <p className="text-sm font-medium text-gray-600 leading-relaxed mb-4">
                    Your stock levels are healthy. 0 items are critically low today.
                  </p>
                  <button onClick={() => setActiveTab('inventory')} className="text-xs font-black text-amber-600 flex items-center gap-1 hover:gap-2 transition-all cursor-pointer">
                    Check Inventory <ChevronRight size={14} />
                  </button>
                </div>

                <div className="bg-gray-50 p-6 rounded-3xl border border-gray-100">
                  <div className="flex items-center gap-4 mb-4">
                    <div className="w-12 h-12 bg-purple-100 text-purple-600 rounded-2xl flex items-center justify-center">
                      <Star size={24} />
                    </div>
                    <div>
                      <p className="font-black text-gray-900">Customer Satisfaction</p>
                      <p className="text-[10px] font-bold text-gray-400">QUALITY INDEX</p>
                    </div>
                  </div>
                  <p className="text-sm font-medium text-gray-600 leading-relaxed mb-4">
                    Based on {analytics.reviews?.totalReviews || 0} reviews, your service quality is rated "Excellent".
                  </p>
                  <button onClick={() => setActiveTab('reviews')} className="text-xs font-black text-purple-600 flex items-center gap-1 hover:gap-2 transition-all cursor-pointer">
                    Read Feedbacks <ChevronRight size={14} />
                  </button>
                </div>
             </div>
           </div>
        </div>

        {/* Quick Actions Side Card */}
        <div className="lg:col-span-4 bg-gray-900 text-white p-8 rounded-[2.5rem] shadow-xl relative overflow-hidden">
           <div className="absolute top-0 left-0 w-full h-full bg-gradient-to-br from-amber-500/10 to-transparent"></div>
           <div className="relative z-10">
             <h3 className="font-bold text-xl mb-6">Quick Tasks</h3>
             <div className="space-y-4">
               {[
                 { label: 'Print QR Codes', icon: Layout, desc: 'Fresh codes for tables', tab: 'hotel' },
                 { label: 'Update Menu', icon: Package, desc: 'Edit prices and items', tab: 'menu' },
                 { label: 'Check Live Bills', icon: Zap, desc: 'Monitor active sessions', tab: 'live' },
                 { label: 'Staff Roster', icon: Users, desc: 'Manage role access', tab: 'staff' },
               ].map((action, i) => (
                 <button key={i} onClick={() => setActiveTab(action.tab)} className="w-full flex items-center gap-4 p-4 rounded-2xl bg-white/5 border border-white/10 hover:bg-white/10 transition text-left group cursor-pointer">
                   <div className="p-2 bg-amber-500/20 text-amber-500 rounded-xl group-hover:scale-110 transition">
                     <action.icon size={18} />
                   </div>
                   <div>
                     <p className="font-bold text-sm">{action.label}</p>
                     <p className="text-[10px] text-gray-500">{action.desc}</p>
                   </div>
                 </button>
               ))}
             </div>
             
             <div className="mt-10 p-6 bg-gradient-to-r from-amber-600 to-amber-500 rounded-3xl">
                <p className="text-white font-black text-sm mb-1 uppercase tracking-wider">Pro Tip</p>
                <p className="text-white/80 text-xs font-medium leading-relaxed">
                  Analyze your peak hours to schedule staff more efficiently and maximize revenue during rush times.
                </p>
             </div>
           </div>
        </div>
      </div>
    </div>
  );
};

export default HotelSaaSDashboard;
